/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.model;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import spypunk.snake.model.Food.Type;

public class SnakeInstance {

    private final Map<Type, Integer> statistics;

    private final List<Point> snakeParts = Lists.newArrayList();

    private int score;

    private int speed;

    private int currentMovementFrame;

    private Direction direction;

    private Optional<Direction> nextDirection = Optional.empty();

    private Food food;

    private int framesSinceLastFood;

    public SnakeInstance() {
        statistics = Lists.newArrayList(Type.values()).stream()
                .collect(Collectors.toMap(foodType -> foodType, foodType -> 0));
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(final int speed) {
        this.speed = speed;
    }

    public int getCurrentMovementFrame() {
        return currentMovementFrame;
    }

    public void setCurrentMovementFrame(final int currentMoveFrame) {
        currentMovementFrame = currentMoveFrame;
    }

    public List<Point> getSnakeParts() {
        return snakeParts;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(final Direction direction) {
        this.direction = direction;
    }

    public Optional<Direction> getNextDirection() {
        return nextDirection;
    }

    public void setNextDirection(final Direction direction) {
        nextDirection = Optional.ofNullable(direction);
    }

    public int getFramesSinceLastFood() {
        return framesSinceLastFood;
    }

    public void setFramesSinceLastFood(final int framesSinceLastFood) {
        this.framesSinceLastFood = framesSinceLastFood;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(final Food food) {
        this.food = food;
    }

    public Map<Type, Integer> getStatistics() {
        return statistics;
    }
}
