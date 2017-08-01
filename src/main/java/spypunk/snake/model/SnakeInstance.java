/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.model;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import spypunk.snake.model.Food.Type;

public class SnakeInstance {

    private final Map<Type, Integer> statistics;

    private final LinkedList<Point> snakeParts = Lists.newLinkedList();

    private int score;

    private int speed;

    private int currentMovementFrame;

    private Direction direction;

    private Optional<Direction> nextDirection = Optional.empty();

    private Food food;

    private int framesSinceLastFoodPopped;

    private boolean foodPopped;

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

    public LinkedList<Point> getSnakeParts() {
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

    public int getFramesSinceLastFoodPopped() {
        return framesSinceLastFoodPopped;
    }

    public void setFramesSinceLastFoodPopped(final int framesSinceLastFoodPopped) {
        this.framesSinceLastFoodPopped = framesSinceLastFoodPopped;
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

    public boolean isFoodPopped() {
        return foodPopped;
    }

    public void setFoodPopped(final boolean foodPopped) {
        this.foodPopped = foodPopped;
    }
}
