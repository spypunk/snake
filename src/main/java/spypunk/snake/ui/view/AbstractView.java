/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.view;

import spypunk.snake.model.Snake;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;

public abstract class AbstractView {

    protected final FontCache fontCache;

    protected final ImageCache imageCache;

    protected final Snake snake;

    protected AbstractView(final FontCache fontCache, final ImageCache imageCache, final Snake snake) {
        this.fontCache = fontCache;
        this.imageCache = imageCache;
        this.snake = snake;
    }
}
