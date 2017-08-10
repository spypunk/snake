/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller.command.cache;

import spypunk.snake.ui.controller.command.SnakeControllerCommand;

public interface SnakeControllerCommandCache {

    public enum SnakeControllerCommandType {
        LEFT,
        RIGHT,
        DOWN,
        UP,
        NEW_GAME,
        PAUSE,
        MUTE,
        DECREASE_VOLUME,
        INCREASE_VOLUME,
        OPEN_PROJECT_URL,
        GAME_OVER,
        FOOD_EATEN
    }

    SnakeControllerCommand getSnakeControllerCommand(SnakeControllerCommandType snakeControllerCommandType);
}
