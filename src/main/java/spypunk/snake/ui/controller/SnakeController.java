/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller;

import spypunk.snake.ui.view.SnakeView;

public interface SnakeController {

    void start();

    void onWindowClosed();

    void onURLOpen();

    void onKeyPressed(int keyCode);

    void onKeyReleased(int keyCode);

    SnakeView getSnakeView();

    void onGameLoopUpdate();
}
