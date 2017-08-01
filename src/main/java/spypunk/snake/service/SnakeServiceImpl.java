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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
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

    private static final int DEFAULT_SPEED = 3;

    private final List<Point> gridLocations = createGridLocations();

    private final Random random = new Random();

    private final Snake snake;

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

        snake.setSnakeInstance(new SnakeInstance());
        snake.setSpeed(DEFAULT_SPEED);
        snake.setDirection(Direction.DOWN);
        snake.getSnakeParts().addAll(snakeParts);
        snake.setState(State.RUNNING);

        popNextFood();
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
            snake.setNextDirection(direction);
        }
    }

    @Override
    public void mute() {
        snake.setMuted(!snake.isMuted());
    }

    private void handleFoodPopped() {
        if (snake.isFoodPopped()) {
            snake.setFoodPopped(false);
        } else {
            incrementFramesSinceLastFoodPopped();
        }
    }

    private void incrementFramesSinceLastFoodPopped() {
        snake.setFramesSinceLastFoodPopped(snake.getFramesSinceLastFoodPopped() + 1);
    }

    private void popNextFood() {
        final List<Point> possibleFoodLocations = Lists.newArrayList(gridLocations);

        possibleFoodLocations.removeAll(snake.getSnakeParts());

        final int foodIndex = random.nextInt(possibleFoodLocations.size());
        final Point foodLocation = possibleFoodLocations.get(foodIndex);
        final Type foodType = random.nextInt(BONUS_FOOD_RANDOM) == 0 ? Type.BONUS : Type.NORMAL;

        snake.setFood(new Food(foodLocation, foodType));
        snake.setFoodPopped(true);
        snake.setFramesSinceLastFoodPopped(0);
    }

    private static List<Point> createGridLocations() {
        final Builder<Point> gridLocations = new ImmutableList.Builder<>();

        IntStream.range(0, SnakeConstants.WIDTH).forEach(
            x -> IntStream.range(0, SnakeConstants.HEIGHT).forEach(y -> gridLocations.add(new Point(x, y))));

        return gridLocations.build();
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
        snake.setCurrentMovementFrame(snake.getCurrentMovementFrame() + 1);
    }

    private void handleBonusFood() {
        final Food food = snake.getFood();
        final Type foodType = food.getType();

        if (Type.BONUS.equals(foodType) && snake.getFramesSinceLastFoodPopped() == BONUS_FOOD_FRAME_LIMIT) {
            popNextFood();
        }
    }

    private void handleDirection() {
        final Optional<Direction> nextDirection = snake.getNextDirection();

        if (!nextDirection.isPresent()) {
            return;
        }

        snake.setDirection(snake.getDirection().apply(nextDirection.get()));
        snake.setNextDirection(null);
    }

    private void moveSnake(final Point nextLocation) {
        final Deque<Point> snakeParts = snake.getSnakeParts();

        snakeParts.addFirst(nextLocation);

        final Food food = snake.getFood();

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
        final Map<Type, Integer> statistics = snake.getStatistics();
        final Integer foodTypeCount = statistics.get(foodType);

        statistics.put(foodType, foodTypeCount + 1);
    }

    private void updateScore(final Type foodType) {
        snake.setScore(snake.getScore() + foodType.getPoints());
    }

    private Point getNextLocation() {
        final Deque<Point> snakeParts = snake.getSnakeParts();
        final Point snakeHeadPart = snakeParts.getFirst();
        final Direction direction = snake.getDirection();

        return direction.apply(snakeHeadPart);
    }

    private boolean canSnakeMove(final Point location) {
        final boolean isLocationOutOfBounds = location.x < 0 || location.x == SnakeConstants.WIDTH
                || location.y < 0
                || location.y == SnakeConstants.HEIGHT;

        return !snake.getSnakeParts().contains(location) && !isLocationOutOfBounds;
    }

    private boolean isTimeToHandleMovement() {
        return snake.getCurrentMovementFrame() == snake.getSpeed();
    }

    private boolean isSnakeRunning() {
        return State.RUNNING.equals(snake.getState());
    }

    private void resetCurrentMovementFrame() {
        snake.setCurrentMovementFrame(0);
    }
}
