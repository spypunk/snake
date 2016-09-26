/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.cache;

import java.awt.Image;

import spypunk.snake.model.Food.Type;
import spypunk.snake.ui.icon.Icon;
import spypunk.snake.ui.snakepart.SnakePart;

public interface ImageCache {

    Image getIcon(Icon icon);

    Image getSnakeImage(SnakePart snakePart);

    Image getFoodImage(Type foodType);
}
