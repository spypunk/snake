/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.service;

import java.awt.Point;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
        if (!isSnakeRunning()) {
            return;
        }

        handleMovement();
        handleBonusFood();
        handleFoodPopped();
    }

    @Override
    public void pause() {
        snake.setState(snake.getState().onPause());
    }

    @Override
    public void updateDirection(final Direction direction) {
        if (isSnakeRunning()) {
            snakeInstance.setNextDirection(direction);
        }
    }

    @Override
    public void mute() {
        snake.setMuted(!snake.isMuted());
    }

    private void handleFoodPopped() {
        if (snakeInstance.isFoodPopped()) {
            snakeInstance.setFoodPopped(false);
        } else {
            incrementFramesSinceLastFoodPopped();
        }
    }

    private void incrementFramesSinceLastFoodPopped() {
        snakeInstance.setFramesSinceLastFoodPopped(snakeInstance.getFramesSinceLastFoodPopped() + 1);
    }

    private void popNextFood() {
        final List<Point> possibleFoodLocations = Lists.newArrayList(gridLocations);

        possibleFoodLocations.removeAll(snakeInstance.getSnakeParts());

        final int foodIndex = random.nextInt(possibleFoodLocations.size());
        final Point foodLocation = possibleFoodLocations.get(foodIndex);
        final Type foodType = random.nextInt(BONUS_FOOD_RANDOM) == 0 ? Type.BONUS : Type.NORMAL;

        snakeInstance.setFood(new Food(foodLocation, foodType));
        snakeInstance.setFoodPopped(true);
        snakeInstance.setFramesSinceLastFoodPopped(0);
    }

    private static List<Point> createGridLocations() {
        final List<Point> gridLocations = Lists.newArrayList();

        IntStream.range(0, SnakeConstants.WIDTH).forEach(
            x -> IntStream.range(0, SnakeConstants.HEIGHT).forEach(y -> gridLocations.add(new Point(x, y))));

        return gridLocations;
    }

    private void handleMovement() {
        if (!isTimeToHandleMovement()) {
            incrementCurrentMovementFrame();
            return;
        }

        handleDirection();

        final Point nextLocation = getNextLocation();

        if (canSnakeMove(nextLocation)) {
            moveSnake(nextLocation);
        } else {
            snake.setState(State.GAME_OVER);
            snake.getSnakeEvents().add(SnakeEvent.GAME_OVER);
        }

        resetCurrentMovementFrame();
    }

    private void incrementCurrentMovementFrame() {
        snakeInstance.setCurrentMovementFrame(snakeInstance.getCurrentMovementFrame() + 1);
    }

    private void handleBonusFood() {
        final Food food = snakeInstance.getFood();
        final Type foodType = food.getType();

        if (Type.BONUS.equals(foodType) && snakeInstance.getFramesSinceLastFoodPopped() == BONUS_FOOD_FRAME_LIMIT) {
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

    private void moveSnake(final Point nextLocation) {
        final Deque<Point> snakeParts = snakeInstance.getSnakeParts();

        snakeParts.addFirst(nextLocation);

        final Food food = snakeInstance.getFood();

        if (food.getLocation().equals(nextLocation)) {
            final Type foodType = food.getType();

            updateScore(foodType);
            updateStatistics(foodType);

            snake.getSnakeEvents().add(SnakeEvent.FOOD_EATEN);

            popNextFood();
        } else {
            snakeParts.removeLast();
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

    private Point getNextLocation() {
        final Deque<Point> snakeParts = snakeInstance.getSnakeParts();
        final Point snakeHeadPart = snakeParts.getFirst();
        final Direction direction = snakeInstance.getDirection();

        return direction.apply(snakeHeadPart);
    }

    private boolean canSnakeMove(final Point location) {
        final boolean isLocationOutOfBounds = location.x < 0 || location.x == SnakeConstants.WIDTH
                || location.y < 0
                || location.y == SnakeConstants.HEIGHT;

        return !snakeInstance.getSnakeParts().contains(location) && !isLocationOutOfBounds;
    }

    private boolean isTimeToHandleMovement() {
        return snakeInstance.getCurrentMovementFrame() == snakeInstance.getSpeed();
    }

    private boolean isSnakeRunning() {
        return State.RUNNING.equals(snake.getState());
    }

    private void resetCurrentMovementFrame() {
        snakeInstance.setCurrentMovementFrame(0);
    }
}
