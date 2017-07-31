/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller;

import javax.inject.Inject;
import javax.inject.Singleton;

import spypunk.snake.guice.SnakeModule.SnakeProvider;
import spypunk.snake.model.Snake;
import spypunk.snake.service.SnakeService;
import spypunk.snake.ui.controller.event.SnakeControllerSnakeEventHandler;
import spypunk.snake.ui.controller.gameloop.SnakeControllerGameLoop;
import spypunk.snake.ui.controller.input.SnakeControllerInputHandler;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.view.SnakeView;

@Singleton
public class SnakeControllerImpl implements SnakeController {

    private final SnakeService snakeService;

    private final SnakeView snakeView;

    private final Snake snake;

    private final SnakeControllerGameLoop snakeControllerGameLoop;

    private final SnakeControllerInputHandler snakeControllerInputHandler;

    private final SnakeControllerSnakeEventHandler snakeControllersnakeEventHandler;

    @Inject
    public SnakeControllerImpl(final SnakeControllerGameLoop snakeControllerGameLoop, final SnakeService snakeService,
            final SnakeControllerInputHandler snakeControllerInputHandler,
            final SnakeControllerSnakeEventHandler snakeControllersnakeEventHandler,
            final @SnakeProvider Snake snake,
            final SnakeView snakeView) {
        this.snakeService = snakeService;
        this.snakeControllerInputHandler = snakeControllerInputHandler;
        this.snakeControllersnakeEventHandler = snakeControllersnakeEventHandler;
        this.snakeControllerGameLoop = snakeControllerGameLoop;
        this.snake = snake;
        this.snakeView = snakeView;
    }

    @Override
    public void start() {
        snakeView.show();

        snakeControllerGameLoop.start();
    }

    @Override
    public void onWindowClosed() {
        snakeControllerGameLoop.stop();
    }

    @Override
    public void onURLOpen() {
        SwingUtils.openURI(snake.getProjectURI());
    }

    @Override
    public void onGameLoopUpdate() {
        snakeControllerInputHandler.handleInputs();

        snakeService.update();

        snakeControllersnakeEventHandler.handleEvents();

        snakeView.update();
    }

    @Override
    public void onKeyPressed(final int keyCode) {
        snakeControllerInputHandler.onKeyPressed(keyCode);
    }

    @Override
    public void onKeyReleased(final int keyCode) {
        snakeControllerInputHandler.onKeyReleased(keyCode);
    }
}
