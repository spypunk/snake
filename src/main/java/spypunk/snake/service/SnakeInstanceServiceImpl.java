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

import javax.inject.Singleton;

import com.google.common.collect.Lists;

import spypunk.snake.constants.SnakeConstants;
import spypunk.snake.model.Direction;
import spypunk.snake.model.Food;
import spypunk.snake.model.Food.Type;
import spypunk.snake.model.Snake;
import spypunk.snake.model.SnakeEvent;
import spypunk.snake.model.SnakeInstance;
import spypunk.snake.model.SnakeInstance.State;

@Singleton
public class SnakeInstanceServiceImpl implements SnakeInstanceService {

    private static final int BONUS_FOOD_RANDOM = 10;

    private static final int BONUS_FOOD_FRAME_LIMIT = 120;

    private final List<Point> gridLocations = createGridLocations();

    private final Random random = new Random();

    @Override
    public void create(final Snake snake) {
        final List<Point> snakeParts = Lists.newArrayList();

        final int x = SnakeConstants.WIDTH / 2;

        snakeParts.add(new Point(x, 2));
        snakeParts.add(new Point(x, 1));
        snakeParts.add(new Point(x, 0));

        final Map<Type, Integer> statistics = Lists.newArrayList(Type.values()).stream()
                .collect(Collectors.toMap(foodType -> foodType, foodType -> 0));

        final SnakeInstance snakeInstance = SnakeInstance.Builder.instance()
                .setSpeed(SnakeConstants.DEFAULT_SPEED).setState(State.RUNNING).setSnakeDirection(Direction.DOWN)
                .setSnakeParts(snakeParts)
                .setStatistics(statistics)
                .build();

        getNextFood(snakeInstance);

        snake.setSnakeInstance(snakeInstance);
    }

    @Override
    public void update(final SnakeInstance snakeInstance) {
        snakeInstance.getSnakeEvents().clear();

        if (!isSnakeInstanceRunning(snakeInstance)) {
            return;
        }

        snakeInstance.setCurrentMovementFrame(snakeInstance.getCurrentMovementFrame() + 1);
        snakeInstance.setFramesSinceLastFood(snakeInstance.getFramesSinceLastFood() + 1);

        handleMovement(snakeInstance);
        handleBonusFood(snakeInstance);
    }

    @Override
    public void pause(final SnakeInstance snakeInstance) {
        snakeInstance.setState(snakeInstance.getState().onPause());
    }

    @Override
    public void updateDirection(final SnakeInstance snakeInstance, final Direction direction) {
        if (isSnakeInstanceRunning(snakeInstance)) {
            snakeInstance.setNewSnakeDirection(Optional.of(direction));
        }
    }

    private void getNextFood(final SnakeInstance snakeInstance) {
        final List<Point> snakeParts = snakeInstance.getSnakeParts();
        final List<Point> foodPossibleLocations = Lists.newArrayList(gridLocations);

        foodPossibleLocations.removeAll(snakeParts);

        final int foodIndex = random.nextInt(foodPossibleLocations.size());

        final Point foodLocation = foodPossibleLocations.get(foodIndex);

        final Type foodType = random.nextInt(BONUS_FOOD_RANDOM) == 0 ? Type.BONUS : Type.NORMAL;

        snakeInstance.setFood(Food.Builder.instance().setLocation(foodLocation).setType(foodType).build());

        snakeInstance.setFramesSinceLastFood(0);
    }

    private static List<Point> createGridLocations() {
        return IntStream.range(0, SnakeConstants.WIDTH).mapToObj(
            x -> IntStream.range(0, SnakeConstants.HEIGHT).mapToObj(y -> new Point(x, y)).collect(Collectors.toList()))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private void handleMovement(final SnakeInstance snakeInstance) {
        if (!isTimeToHandleMovement(snakeInstance)) {
            return;
        }

        handleDirection(snakeInstance);

        if (canSnakeMove(snakeInstance)) {
            moveSnake(snakeInstance);
        } else {
            snakeInstance.setState(State.GAME_OVER);
            snakeInstance.getSnakeEvents().add(SnakeEvent.GAME_OVER);
        }

        resetCurrentMovementFrame(snakeInstance);
    }

    private void handleBonusFood(final SnakeInstance snakeInstance) {
        final Food food = snakeInstance.getFood();
        final Type foodType = food.getType();

        if (Type.BONUS.equals(foodType) && snakeInstance.getFramesSinceLastFood() == BONUS_FOOD_FRAME_LIMIT) {
            getNextFood(snakeInstance);
        }
    }

    private void handleDirection(final SnakeInstance snakeInstance) {
        final Optional<Direction> newSnakeDirection = snakeInstance.getNewSnakeDirection();

        if (!newSnakeDirection.isPresent()) {
            return;
        }

        snakeInstance.setSnakeDirection(snakeInstance.getSnakeDirection().apply(newSnakeDirection.get()));
        snakeInstance.setNewSnakeDirection(Optional.empty());
    }

    private void moveSnake(final SnakeInstance snakeInstance) {
        final Point newLocation = getSnakeHeadPartNextLocation(snakeInstance);
        final List<Point> snakeParts = snakeInstance.getSnakeParts();
        final List<Point> newSnakeParts = Lists.newArrayList();

        newSnakeParts.add(newLocation);
        newSnakeParts.addAll(snakeParts.subList(0, snakeParts.size() - 1));

        snakeInstance.setSnakeParts(newSnakeParts);

        final Food food = snakeInstance.getFood();
        final Type foodType = food.getType();

        if (food.getLocation().equals(newLocation)) {
            newSnakeParts.add(snakeParts.get(snakeParts.size() - 1));
            updateScore(snakeInstance, foodType);
            updateStatistics(snakeInstance, foodType);
            snakeInstance.getSnakeEvents().add(SnakeEvent.FOOD_EATEN);
            getNextFood(snakeInstance);
        }
    }

    private void updateStatistics(final SnakeInstance snakeInstance, final Type foodType) {
        final Map<Type, Integer> statistics = snakeInstance.getStatistics();
        final Integer foodTypeCount = statistics.get(foodType);
        statistics.put(foodType, foodTypeCount + 1);
    }

    private void updateScore(final SnakeInstance snakeInstance, final Type foodType) {
        snakeInstance.setScore(snakeInstance.getScore() + foodType.getPoints());
    }

    private Point getSnakeHeadPartNextLocation(final SnakeInstance snakeInstance) {
        final List<Point> snakeParts = snakeInstance.getSnakeParts();
        final Point snakeHeadPart = snakeParts.get(0);
        final Direction direction = snakeInstance.getSnakeDirection();

        return direction.apply(snakeHeadPart);
    }

    private boolean canSnakeMove(final SnakeInstance snakeInstance) {
        final Point newLocation = getSnakeHeadPartNextLocation(snakeInstance);

        final boolean isLocationOutOfBounds = newLocation.x < 0 || newLocation.x == SnakeConstants.WIDTH
                || newLocation.y < 0
                || newLocation.y == SnakeConstants.HEIGHT;

        return !snakeInstance.getSnakeParts().contains(newLocation) && !isLocationOutOfBounds;
    }

    private boolean isTimeToHandleMovement(final SnakeInstance snakeInstance) {
        return snakeInstance.getCurrentMovementFrame() > snakeInstance.getSpeed();
    }

    private boolean isSnakeInstanceRunning(final SnakeInstance snakeInstance) {
        return isSnakeInstanceState(snakeInstance, State.RUNNING);
    }

    private boolean isSnakeInstanceState(final SnakeInstance snakeInstance, final State state) {
        return state.equals(snakeInstance.getState());
    }

    private void resetCurrentMovementFrame(final SnakeInstance snakeInstance) {
        snakeInstance.setCurrentMovementFrame(0);
    }
}
