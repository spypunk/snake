/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.factory;

import spypunk.snake.model.Direction;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;

public interface SnakeControllerCommandFactory {

    SnakeControllerCommand createNewGameCommand();

    SnakeControllerCommand createPauseCommand();

    SnakeControllerCommand createDirectionCommand(Direction direction);

    SnakeControllerCommand createMuteCommand();

    SnakeControllerCommand createIncreaseVolumeCommand();

    SnakeControllerCommand createDecreaseVolumeCommand();

    SnakeControllerCommand createGameOverCommand();

    SnakeControllerCommand createFoodEatenCommand();
}
