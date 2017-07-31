/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.service;

import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Lists;

import spypunk.snake.constants.SnakeConstants;
import spypunk.snake.guice.SnakeModule.SnakeProvider;
import spypunk.snake.model.Direction;
import spypunk.snake.model.Food;
import spypunk.snake.model.Food.Type;
import spypunk.snake.model.Snake;
import spypunk.snake.model.Snake.State;
import spypunk.snake.model.SnakeEvent;
import spypunk.snake.model.SnakeInstance;

@Singleton
public class SnakeServiceImpl implements SnakeService {

    private static final int BONUS_FOOD_RANDOM = 10;

    private static final int BONUS_FOOD_FRAME_LIMIT = 120;

    private final List<Point> gridLocations = createGridLocations();

    private final Random random = new Random();

    private final Snake snake;

    private SnakeInstance snakeInstance;

    @Inject
    public SnakeServiceImpl(@SnakeProvider final Snake snake) {
        this.snake = snake;
    }

    @Override
    public void start() {
        final List<Point> snakeParts = Lists.newArrayList();

        final int x = SnakeConstants.WIDTH / 2;

        snakeParts.add(new Point(x, 2));
        snakeParts.add(new Point(x, 1));
        snakeParts.add(new Point(x, 0));

        snakeInstance = new SnakeInstance();

        snakeInstance.setSpeed(SnakeConstants.DEFAULT_SPEED);
        snakeInstance.setDirection(Direction.DOWN);
        snakeInstance.getSnakeParts().addAll(snakeParts);

        popNextFood();

        snake.setSnakeInstance(snakeInstance);
        snake.setState(State.RUNNING);
    }

    @Override
    public void update() {
        if (!isSnakeInstanceRunning()) {
            return;
        }

        snakeInstance.setCurrentMovementFrame(snakeInstance.getCurrentMovementFrame() + 1);
        snakeInstance.setFramesSinceLastFood(snakeInstance.getFramesSinceLastFood() + 1);

        handleMovement();
        handleBonusFood();
    }

    @Override
    public void pause() {
        snake.setState(snake.getState().onPause());
    }

    @Override
    public void updateDirection(final Direction direction) {
        if (isSnakeInstanceRunning()) {
            snakeInstance.setNextDirection(direction);
        }
    }

    @Override
    public void mute() {
        snake.setMuted(!snake.isMuted());
    }

    private void popNextFood() {
        final List<Point> snakeParts = snakeInstance.getSnakeParts();
        final List<Point> foodPossibleLocations = Lists.newArrayList(gridLocations);

        foodPossibleLocations.removeAll(snakeParts);

        final int foodIndex = random.nextInt(foodPossibleLocations.size());

        final Point foodLocation = foodPossibleLocations.get(foodIndex);

        final Type foodType = random.nextInt(BONUS_FOOD_RANDOM) == 0 ? Type.BONUS : Type.NORMAL;

        snakeInstance.setFood(new Food(foodLocation, foodType));

        snakeInstance.setFramesSinceLastFood(0);
    }

    private static List<Point> createGridLocations() {
        return IntStream.range(0, SnakeConstants.WIDTH).mapToObj(
            x -> IntStream.range(0, SnakeConstants.HEIGHT).mapToObj(y -> new Point(x, y)).collect(Collectors.toList()))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private void handleMovement() {
        if (!isTimeToHandleMovement()) {
            return;
        }

        handleDirection();

        if (canSnakeMove()) {
            moveSnake();
        } else {
            snake.setState(State.GAME_OVER);
            snake.getSnakeEvents().add(SnakeEvent.GAME_OVER);
        }

        resetCurrentMovementFrame();
    }

    private void handleBonusFood() {
        final Food food = snakeInstance.getFood();
        final Type foodType = food.getType();

        if (Type.BONUS.equals(foodType) && snakeInstance.getFramesSinceLastFood() == BONUS_FOOD_FRAME_LIMIT) {
            popNextFood();
        }
    }

    private void handleDirection() {
        final Optional<Direction> nextDirection = snakeInstance.getNextDirection();

        if (!nextDirection.isPresent()) {
            return;
        }

        snakeInstance.setDirection(snakeInstance.getDirection().apply(nextDirection.get()));
        snakeInstance.setNextDirection(null);
    }

    private void moveSnake() {
        final Point newLocation = getSnakeHeadPartNextLocation();
        final List<Point> snakeParts = snakeInstance.getSnakeParts();
        final List<Point> newSnakeParts = Lists.newArrayList();

        newSnakeParts.add(newLocation);
        newSnakeParts.addAll(snakeParts.subList(0, snakeParts.size() - 1));

        snakeParts.clear();
        snakeParts.addAll(newSnakeParts);

        final Food food = snakeInstance.getFood();

        if (food.getLocation().equals(newLocation)) {
            final Type foodType = food.getType();

            snakeParts.add(snakeParts.get(snakeParts.size() - 1));

            updateScore(foodType);
            updateStatistics(foodType);

            snake.getSnakeEvents().add(SnakeEvent.FOOD_EATEN);

            popNextFood();
        }
    }

    private void updateStatistics(final Type foodType) {
        final Map<Type, Integer> statistics = snakeInstance.getStatistics();
        final Integer foodTypeCount = statistics.get(foodType);

        statistics.put(foodType, foodTypeCount + 1);
    }

    private void updateScore(final Type foodType) {
        snakeInstance.setScore(snakeInstance.getScore() + foodType.getPoints());
    }

    private Point getSnakeHeadPartNextLocation() {
        final List<Point> snakeParts = snakeInstance.getSnakeParts();
        final Point snakeHeadPart = snakeParts.get(0);
        final Direction direction = snakeInstance.getDirection();

        return direction.apply(snakeHeadPart);
    }

    private boolean canSnakeMove() {
        final Point newLocation = getSnakeHeadPartNextLocation();

        final boolean isLocationOutOfBounds = newLocation.x < 0 || newLocation.x == SnakeConstants.WIDTH
                || newLocation.y < 0
                || newLocation.y == SnakeConstants.HEIGHT;

        return !snakeInstance.getSnakeParts().contains(newLocation) && !isLocationOutOfBounds;
    }

    private boolean isTimeToHandleMovement() {
        return snakeInstance.getCurrentMovementFrame() == snakeInstance.getSpeed();
    }

    private boolean isSnakeInstanceRunning() {
        return State.RUNNING.equals(snake.getState());
    }

    private void resetCurrentMovementFrame() {
        snakeInstance.setCurrentMovementFrame(0);
    }
}
