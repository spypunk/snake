/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.model;

import java.awt.Point;

public class Food {

    private final Point location;

    private final Type type;

    public enum Type {
        NORMAL {
            @Override
            public int getPoints() {
                return 10;
            }
        },
        BONUS {
            @Override
            public int getPoints() {
                return 50;
            }
        };

        public abstract int getPoints();
    }

    public Food(final Point location, final Type type) {
        this.location = location;
        this.type = type;
    }

    public Point getLocation() {
        return location;
    }

    public Type getType() {
        return type;
    }
}
