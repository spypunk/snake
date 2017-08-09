/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller;

import javax.inject.Inject;
import javax.inject.Singleton;

import spypunk.snake.ui.controller.gameloop.SnakeControllerGameLoop;
import spypunk.snake.ui.controller.input.SnakeControllerInputHandler;
import spypunk.snake.ui.view.SnakeMainView;

@Singleton
public class SnakeControllerImpl implements SnakeController {

    private final SnakeMainView snakeMainView;

    private final SnakeControllerGameLoop snakeControllerGameLoop;

    private final SnakeControllerInputHandler snakeControllerInputHandler;

    @Inject
    public SnakeControllerImpl(final SnakeControllerGameLoop snakeControllerGameLoop,
            final SnakeControllerInputHandler snakeControllerInputHandler,
            final SnakeMainView snakeMainView) {
        this.snakeControllerInputHandler = snakeControllerInputHandler;
        this.snakeControllerGameLoop = snakeControllerGameLoop;
        this.snakeMainView = snakeMainView;
    }

    @Override
    public void start() {
        snakeMainView.show();
    }

    @Override
    public void onWindowOpened() {
        snakeControllerGameLoop.start();
    }

    @Override
    public void onWindowClosed() {
        snakeControllerGameLoop.stop();
    }

    @Override
    public void onProjectURIClicked() {
        snakeControllerInputHandler.onProjectURIClicked();
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
