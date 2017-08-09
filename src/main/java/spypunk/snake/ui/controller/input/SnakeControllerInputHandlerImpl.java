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

import spypunk.snake.model.Direction;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;
import spypunk.snake.ui.factory.SnakeControllerCommandFactory;

@Singleton
public class SnakeControllerInputHandlerImpl implements SnakeControllerInputHandler {

    private final Set<CommandType> triggeredCommands = Sets.newConcurrentHashSet();

    private final Map<Integer, CommandType> keyEventCommandTypes = Maps.newHashMap();

    private final Map<CommandType, SnakeControllerCommand> snakeControllerCommands = Maps.newHashMap();

    private enum InputType {
        KEY_PRESSED,
        KEY_RELEASED,
        MOUSE_CLICKED
    }

    private enum CommandType {
        LEFT(InputType.KEY_PRESSED),
        RIGHT(InputType.KEY_PRESSED),
        UP(InputType.KEY_PRESSED),
        DOWN(InputType.KEY_PRESSED),
        NEW_GAME(InputType.KEY_RELEASED),
        PAUSE(InputType.KEY_RELEASED),
        MUTE(InputType.KEY_RELEASED),
        DECREASE_VOLUME(InputType.KEY_RELEASED),
        INCREASE_VOLUME(InputType.KEY_RELEASED),
        OPEN_PROJECT_URL(InputType.MOUSE_CLICKED);

        private final InputType inputType;

        private CommandType(final InputType inputType) {
            this.inputType = inputType;
        }
    }

    @Inject
    public SnakeControllerInputHandlerImpl(final SnakeControllerCommandFactory snakeControllerCommandFactory) {
        keyEventCommandTypes.put(KeyEvent.VK_LEFT, CommandType.LEFT);
        keyEventCommandTypes.put(KeyEvent.VK_RIGHT, CommandType.RIGHT);
        keyEventCommandTypes.put(KeyEvent.VK_UP, CommandType.UP);
        keyEventCommandTypes.put(KeyEvent.VK_DOWN, CommandType.DOWN);
        keyEventCommandTypes.put(KeyEvent.VK_SPACE, CommandType.NEW_GAME);
        keyEventCommandTypes.put(KeyEvent.VK_P, CommandType.PAUSE);
        keyEventCommandTypes.put(KeyEvent.VK_M, CommandType.MUTE);
        keyEventCommandTypes.put(KeyEvent.VK_PAGE_UP, CommandType.INCREASE_VOLUME);
        keyEventCommandTypes.put(KeyEvent.VK_PAGE_DOWN, CommandType.DECREASE_VOLUME);

        snakeControllerCommands.put(CommandType.LEFT,
            snakeControllerCommandFactory.createDirectionCommand(Direction.LEFT));

        snakeControllerCommands.put(CommandType.RIGHT,
            snakeControllerCommandFactory.createDirectionCommand(Direction.RIGHT));

        snakeControllerCommands.put(CommandType.UP, snakeControllerCommandFactory.createDirectionCommand(Direction.UP));

        snakeControllerCommands.put(CommandType.DOWN,
            snakeControllerCommandFactory.createDirectionCommand(Direction.DOWN));

        snakeControllerCommands.put(CommandType.NEW_GAME, snakeControllerCommandFactory.createNewGameCommand());

        snakeControllerCommands.put(CommandType.PAUSE, snakeControllerCommandFactory.createPauseCommand());

        snakeControllerCommands.put(CommandType.MUTE, snakeControllerCommandFactory.createMuteCommand());

        snakeControllerCommands.put(CommandType.INCREASE_VOLUME,
            snakeControllerCommandFactory.createIncreaseVolumeCommand());

        snakeControllerCommands.put(CommandType.DECREASE_VOLUME,
            snakeControllerCommandFactory.createDecreaseVolumeCommand());

        snakeControllerCommands.put(CommandType.OPEN_PROJECT_URL,
            snakeControllerCommandFactory.createOpenProjectURLCommand());
    }

    @Override
    public void onKeyPressed(final int keyCode) {
        onKey(keyCode, InputType.KEY_PRESSED);
    }

    @Override
    public void onKeyReleased(final int keyCode) {
        onKey(keyCode, InputType.KEY_RELEASED);
    }

    @Override
    public void onProjectURLClicked() {
        triggeredCommands.add(CommandType.OPEN_PROJECT_URL);
    }

    @Override
    public void handleInputs() {
        triggeredCommands.stream().map(snakeControllerCommands::get).forEach(SnakeControllerCommand::execute);

        triggeredCommands.clear();
    }

    private void onKey(final int keyCode, final InputType inputType) {
        if (keyEventCommandTypes.containsKey(keyCode)) {

            final CommandType commandType = keyEventCommandTypes.get(keyCode);

            if (inputType.equals(commandType.inputType)) {
                triggeredCommands.add(commandType);
            }
        }
    }
}
