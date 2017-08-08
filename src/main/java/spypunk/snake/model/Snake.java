/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.model;

import java.awt.Point;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import spypunk.snake.model.Food.Type;

public class Snake {

    private final String name;

    private final String version;

    private final URI projectURI;

    private SnakeInstance snakeInstance;

    private boolean muted;

    public enum State {
        RUNNING {
            @Override
            public State onPause() {
                return PAUSED;
            }
        },
        PAUSED {
            @Override
            public State onPause() {
                return RUNNING;
            }
        },
        GAME_OVER,
        STOPPED;

        public State onPause() {
            return this;
        }
    }

    public Snake(final String name, final String version, final URI projectURI) {
        this.name = name;
        this.version = version;
        this.projectURI = projectURI;

        snakeInstance = new SnakeInstance();
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public URI getProjectURI() {
        return projectURI;
    }

    public void setSnakeInstance(final SnakeInstance snakeInstance) {
        this.snakeInstance = snakeInstance;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(final boolean muted) {
        this.muted = muted;
    }

    public List<SnakeEvent> getSnakeEvents() {
        return snakeInstance.getSnakeEvents();
    }

    public int getScore() {
        return snakeInstance.getScore();
    }

    public void setScore(final int score) {
        snakeInstance.setScore(score);
    }

    public int getSpeed() {
        return snakeInstance.getSpeed();
    }

    public void setSpeed(final int speed) {
        snakeInstance.setSpeed(speed);
    }

    public int getCurrentMovementFrame() {
        return snakeInstance.getCurrentMovementFrame();
    }

    public void setCurrentMovementFrame(final int currentMoveFrame) {
        snakeInstance.setCurrentMovementFrame(currentMoveFrame);
    }

    public LinkedList<Point> getSnakePartLocations() {
        return snakeInstance.getSnakePartLocations();
    }

    public Direction getDirection() {
        return snakeInstance.getDirection();
    }

    public void setDirection(final Direction direction) {
        snakeInstance.setDirection(direction);
    }

    public Optional<Direction> getNextDirection() {
        return snakeInstance.getNextDirection();
    }

    public void setNextDirection(final Direction direction) {
        snakeInstance.setNextDirection(direction);
    }

    public int getFramesSinceLastFoodPopped() {
        return snakeInstance.getFramesSinceLastFoodPopped();
    }

    public void setFramesSinceLastFoodPopped(final int framesSinceLastFoodPopped) {
        snakeInstance.setFramesSinceLastFoodPopped(framesSinceLastFoodPopped);
    }

    public Food getFood() {
        return snakeInstance.getFood();
    }

    public void setFood(final Food food) {
        snakeInstance.setFood(food);
    }

    public Map<Type, Integer> getStatistics() {
        return snakeInstance.getStatistics();
    }

    public boolean isFoodPopped() {
        return snakeInstance.isFoodPopped();
    }

    public void setFoodPopped(final boolean foodPopped) {
        snakeInstance.setFoodPopped(foodPopped);
    }

    public State getState() {
        return snakeInstance.getState();
    }

    public void setState(final State state) {
        snakeInstance.setState(state);
    }
}
