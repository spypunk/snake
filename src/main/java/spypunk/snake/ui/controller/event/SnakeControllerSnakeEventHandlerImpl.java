/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller.event;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Maps;

import spypunk.snake.guice.SnakeModule.SnakeProvider;
import spypunk.snake.model.Snake;
import spypunk.snake.model.SnakeEvent;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;
import spypunk.snake.ui.factory.SnakeControllerCommandFactory;

@Singleton
public class SnakeControllerSnakeEventHandlerImpl implements SnakeControllerSnakeEventHandler {

    private final Map<SnakeEvent, SnakeControllerCommand> snakeControllerCommands = Maps
            .newHashMap();

    private final Snake snake;

    @Inject
    public SnakeControllerSnakeEventHandlerImpl(final SnakeControllerCommandFactory snakeControllerCommandFactory,
            final @SnakeProvider Snake snake) {

        this.snake = snake;

        snakeControllerCommands.put(SnakeEvent.GAME_OVER,
            snakeControllerCommandFactory.createGameOverCommand());

        snakeControllerCommands.put(SnakeEvent.FOOD_EATEN,
            snakeControllerCommandFactory.createFoodEatenCommand());
    }

    @Override
    public void handleEvents() {
        final List<SnakeEvent> tetrisEvents = snake.getSnakeEvents();

        tetrisEvents.stream().map(snakeControllerCommands::get).forEach(SnakeControllerCommand::execute);

        tetrisEvents.clear();
    }

}
