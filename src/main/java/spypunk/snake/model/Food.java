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
