/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller.input;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import spypunk.snake.ui.controller.command.SnakeControllerCommand;
import spypunk.snake.ui.controller.command.cache.SnakeControllerCommandCache;
import spypunk.snake.ui.controller.command.cache.SnakeControllerCommandCache.SnakeControllerCommandType;

@Singleton
public class SnakeControllerInputHandlerImpl implements SnakeControllerInputHandler {

    private final Set<SnakeControllerCommandType> triggeredCommands = Sets.newConcurrentHashSet();

    private final Map<Integer, SnakeControllerCommandType> pressedKeyEventCommandTypes = Maps.newHashMap();

    private final Map<Integer, SnakeControllerCommandType> releasedKeyEventCommandTypes = Maps.newHashMap();

    private final SnakeControllerCommandCache snakeControllerCommandCache;

    @Inject
    public SnakeControllerInputHandlerImpl(final SnakeControllerCommandCache snakeControllerCommandCache) {
        this.snakeControllerCommandCache = snakeControllerCommandCache;

        pressedKeyEventCommandTypes.put(KeyEvent.VK_LEFT, SnakeControllerCommandType.LEFT);
        pressedKeyEventCommandTypes.put(KeyEvent.VK_RIGHT, SnakeControllerCommandType.RIGHT);
        pressedKeyEventCommandTypes.put(KeyEvent.VK_DOWN, SnakeControllerCommandType.DOWN);
        pressedKeyEventCommandTypes.put(KeyEvent.VK_UP, SnakeControllerCommandType.UP);

        releasedKeyEventCommandTypes.put(KeyEvent.VK_SPACE, SnakeControllerCommandType.NEW_GAME);
        releasedKeyEventCommandTypes.put(KeyEvent.VK_P, SnakeControllerCommandType.PAUSE);
        releasedKeyEventCommandTypes.put(KeyEvent.VK_M, SnakeControllerCommandType.MUTE);
        releasedKeyEventCommandTypes.put(KeyEvent.VK_PAGE_UP, SnakeControllerCommandType.INCREASE_VOLUME);
        releasedKeyEventCommandTypes.put(KeyEvent.VK_PAGE_DOWN, SnakeControllerCommandType.DECREASE_VOLUME);
    }

    @Override
    public void onKeyPressed(final int keyCode) {
        onKey(keyCode, pressedKeyEventCommandTypes);
    }

    @Override
    public void onKeyReleased(final int keyCode) {
        onKey(keyCode, releasedKeyEventCommandTypes);
    }

    @Override
    public void onProjectURLClicked() {
        triggeredCommands.add(SnakeControllerCommandType.OPEN_PROJECT_URL);
    }

    @Override
    public void handleInputs() {
        if (triggeredCommands.isEmpty()) {
            return;
        }

        triggeredCommands.stream()
                .map(snakeControllerCommandCache::getSnakeControllerCommand)
                .forEach(SnakeControllerCommand::execute);

        triggeredCommands.clear();
    }

    private void onKey(final int keyCode, final Map<Integer, SnakeControllerCommandType> keyEventCommandTypes) {
        if (keyEventCommandTypes.containsKey(keyCode)) {
            final SnakeControllerCommandType commandType = keyEventCommandTypes.get(keyCode);

            triggeredCommands.add(commandType);
        }
    }
}
