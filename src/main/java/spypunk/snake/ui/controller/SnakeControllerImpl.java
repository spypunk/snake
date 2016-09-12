/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.collections4.CollectionUtils;

import spypunk.snake.controller.gameloop.SnakeControllerGameLoop;
import spypunk.snake.factory.SnakeFactory;
import spypunk.snake.model.Snake;
import spypunk.snake.model.SnakeEvent;
import spypunk.snake.model.SnakeInstance;
import spypunk.snake.service.SnakeInstanceService;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;
import spypunk.snake.ui.controller.event.SnakeControllerSnakeEventHandler;
import spypunk.snake.ui.controller.input.SnakeControllerInputHandler;
import spypunk.snake.ui.factory.SnakeViewFactory;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.view.SnakeView;

@Singleton
public class SnakeControllerImpl implements SnakeController {

    private final SnakeInstanceService snakeInstanceService;

    private final SnakeView snakeView;

    private final Snake snake;

    private final SnakeControllerGameLoop snakeControllerGameLoop;

    private final SnakeControllerInputHandler snakeControllerInputHandler;

    private final SnakeControllerSnakeEventHandler snakeControllersnakeEventHandler;

    @Inject
    public SnakeControllerImpl(final SnakeFactory snakeFactory, final SnakeViewFactory snakeViewFactory,
            final SnakeControllerGameLoop snakeControllerGameLoop, final SnakeInstanceService snakeInstanceService,
            final SnakeControllerInputHandler snakeControllerInputHandler,
            final SnakeControllerSnakeEventHandler snakeControllersnakeEventHandler) {
        this.snakeInstanceService = snakeInstanceService;
        this.snakeControllerInputHandler = snakeControllerInputHandler;
        this.snakeControllersnakeEventHandler = snakeControllersnakeEventHandler;
        this.snakeControllerGameLoop = snakeControllerGameLoop;

        snake = snakeFactory.createSnake();
        snakeView = snakeViewFactory.createsnakeView(snake);
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
        executesnakeControllerCommands(snakeControllerInputHandler.handleInputs());

        snakeControllerInputHandler.reset();

        final SnakeInstance snakeInstance = snake.getSnakeInstance();

        if (snakeInstance != null) {
            snakeInstanceService.update(snakeInstance);

            final List<SnakeEvent> snakeEvents = snakeInstance.getSnakeEvents();

            executesnakeControllerCommands(
                snakeControllersnakeEventHandler.handleEvents(snakeEvents));
        }

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

    @Override
    public SnakeView getSnakeView() {
        return snakeView;
    }

    private void executesnakeControllerCommands(final Collection<SnakeControllerCommand> snakeControllerCommands) {
        if (CollectionUtils.isEmpty(snakeControllerCommands)) {
            return;
        }

        snakeControllerCommands.forEach(snakeControllerCommand -> snakeControllerCommand.execute(snake));
    }
}
