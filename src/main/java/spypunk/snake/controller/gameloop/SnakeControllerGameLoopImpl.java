/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.controller.gameloop;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import spypunk.snake.ui.controller.SnakeController;

@Singleton
public final class SnakeControllerGameLoopImpl implements SnakeControllerGameLoop, Runnable {

    private static final int TICKS_PER_SECOND = 60;

    private static final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;

    private final ExecutorService executorService;

    private final SnakeController snakeController;

    private volatile boolean running;

    @Inject
    public SnakeControllerGameLoopImpl(final SnakeController snakeController) {
        executorService = Executors.newSingleThreadExecutor();
        this.snakeController = snakeController;
    }

    @Override
    public void start() {
        running = true;
        executorService.execute(this);
    }

    @Override
    public void stop() {
        running = false;
        executorService.shutdown();
    }

    @Override
    public void run() {
        long lastTick = System.currentTimeMillis();

        while (running) {
            long newTick = System.currentTimeMillis();

            for (; newTick - lastTick < SKIP_TICKS; newTick = System
                    .currentTimeMillis()) {
                // Do nothing here
            }

            lastTick = newTick;

            snakeController.onGameLoopUpdate();
        }
    }
}
