/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller.gameloop;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spypunk.snake.service.SnakeService;
import spypunk.snake.ui.controller.event.SnakeControllerSnakeEventHandler;
import spypunk.snake.ui.controller.input.SnakeControllerInputHandler;
import spypunk.snake.ui.view.SnakeMainView;

@Singleton
public final class SnakeControllerGameLoopImpl implements SnakeControllerGameLoop, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnakeControllerGameLoopImpl.class);

    private static final int TICKS_PER_SECOND = 60;

    private static final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;

    private final ExecutorService executorService;

    private final SnakeService snakeService;

    private final SnakeMainView snakeMainView;

    private final SnakeControllerInputHandler snakeControllerInputHandler;

    private final SnakeControllerSnakeEventHandler snakeControllersnakeEventHandler;

    private volatile boolean running;

    @Inject
    public SnakeControllerGameLoopImpl(final SnakeService snakeService, final SnakeMainView snakeMainView,
            final SnakeControllerInputHandler snakeControllerInputHandler,
            final SnakeControllerSnakeEventHandler snakeControllersnakeEventHandler) {
        this.snakeService = snakeService;
        this.snakeMainView = snakeMainView;
        this.snakeControllerInputHandler = snakeControllerInputHandler;
        this.snakeControllersnakeEventHandler = snakeControllersnakeEventHandler;

        executorService = Executors
                .newSingleThreadExecutor(runnable -> new Thread(runnable, "SnakeControllerGameLoop"));
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
        while (running) {
            long currentTick = System.currentTimeMillis();

            update();

            for (final long nextTick = currentTick + SKIP_TICKS; currentTick < nextTick; currentTick = System
                    .currentTimeMillis()) {
                waitMore();
            }
        }
    }

    private void update() {
        snakeControllerInputHandler.handleInputs();

        snakeService.update();

        snakeControllersnakeEventHandler.handleEvents();

        snakeMainView.update();
    }

    private void waitMore() {
        try {
            Thread.sleep(1);
        } catch (final InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
            stop();
        }
    }
}
