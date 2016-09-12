/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller.event;

import java.util.List;

import spypunk.snake.model.SnakeEvent;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;

@FunctionalInterface
public interface SnakeControllerSnakeEventHandler {

    List<SnakeControllerCommand> handleEvents(List<SnakeEvent> snakeEvents);
}
