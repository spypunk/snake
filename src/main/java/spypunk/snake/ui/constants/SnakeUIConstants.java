/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.constants;

import java.awt.Color;

public final class SnakeUIConstants {

    public static final int CELL_SIZE = 20;

    public static final Color DEFAULT_FONT_COLOR = Color.LIGHT_GRAY;

    public static final Color DEFAULT_BORDER_COLOR = Color.GRAY;

    public static final String ZERO = "0".intern();

    private SnakeUIConstants() {
        throw new IllegalAccessError();
    }
}
