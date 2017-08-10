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
import spypunk.snake.ui.controller.command.cache.SnakeControllerCommandCache;
import spypunk.snake.ui.controller.command.cache.SnakeControllerCommandCache.SnakeControllerCommandType;

@Singleton
public class SnakeControllerSnakeEventHandlerImpl implements SnakeControllerSnakeEventHandler {

    private final Map<SnakeEvent, SnakeControllerCommandType> snakeControllerCommandTypes = Maps
            .newHashMap();

    private final Snake snake;

    private final SnakeControllerCommandCache snakeControllerCommandCache;

    @Inject
    public SnakeControllerSnakeEventHandlerImpl(final SnakeControllerCommandCache snakeControllerCommandCache,
            final @SnakeProvider Snake snake) {

        this.snake = snake;
        this.snakeControllerCommandCache = snakeControllerCommandCache;

        snakeControllerCommandTypes.put(SnakeEvent.GAME_OVER, SnakeControllerCommandType.GAME_OVER);
        snakeControllerCommandTypes.put(SnakeEvent.FOOD_EATEN, SnakeControllerCommandType.FOOD_EATEN);
    }

    @Override
    public void handleEvents() {
        final List<SnakeEvent> snakeEvents = snake.getSnakeEvents();

        if (snakeEvents.isEmpty()) {
            return;
        }

        snakeEvents.stream()
                .map(snakeControllerCommandTypes::get)
                .map(snakeControllerCommandCache::getSnakeControllerCommand)
                .forEach(SnakeControllerCommand::execute);

        snakeEvents.clear();
    }
}
