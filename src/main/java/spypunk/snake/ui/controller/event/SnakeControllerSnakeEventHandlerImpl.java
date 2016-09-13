/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Maps;

import spypunk.snake.model.SnakeEvent;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;
import spypunk.snake.ui.factory.SnakeControllerCommandFactory;

@Singleton
public class SnakeControllerSnakeEventHandlerImpl implements SnakeControllerSnakeEventHandler {

    private final Map<SnakeEvent, Supplier<SnakeControllerCommand>> snakeControllerCommands = Maps
            .newHashMap();

    @Inject
    public SnakeControllerSnakeEventHandlerImpl(final SnakeControllerCommandFactory snakeControllerCommandFactory) {
        snakeControllerCommands.put(SnakeEvent.GAME_OVER,
            snakeControllerCommandFactory::createGameOverSnakeControllerCommand);

        snakeControllerCommands.put(SnakeEvent.FOOD_EATEN,
            snakeControllerCommandFactory::createFoodEatenSnakeControllerCommand);
    }

    @Override
    public List<SnakeControllerCommand> handleEvents(final List<SnakeEvent> snakeEvents) {
        if (CollectionUtils.isEmpty(snakeEvents)) {
            return Collections.emptyList();
        }

        return snakeEvents.stream().map(snakeControllerCommands::get).map(Supplier::get).collect(Collectors.toList());
    }

}
