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

    private Point location;

    private Type type;

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

    public static final class Builder {

        private final Food food = new Food();

        private Builder() {
        }

        public static Builder instance() {
            return new Builder();
        }

        public Builder setLocation(final Point location) {
            food.setLocation(location);
            return this;
        }

        public Builder setType(final Type type) {
            food.setType(type);
            return this;
        }

        public Food build() {
            return food;
        }
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(final Point location) {
        this.location = location;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }
}
