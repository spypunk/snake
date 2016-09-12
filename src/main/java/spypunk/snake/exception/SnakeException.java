/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.exception;

public class SnakeException extends RuntimeException {

    private static final long serialVersionUID = -2863969090656932325L;

    public SnakeException(final Throwable cause) {
        super(cause);
    }
}
