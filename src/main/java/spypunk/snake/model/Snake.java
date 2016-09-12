/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.model;

import java.net.URI;

public class Snake {

    private String name;

    private String version;

    private URI projectURI;

    private SnakeInstance snakeInstance;

    public static final class Builder {

        private final Snake snake = new Snake();

        private Builder() {
        }

        public static Builder instance() {
            return new Builder();
        }

        public Builder setName(final String name) {
            snake.setName(name);
            return this;
        }

        public Builder setVersion(final String version) {
            snake.setVersion(version);
            return this;
        }

        public Builder setProjectURI(final URI projectURI) {
            snake.setProjectURI(projectURI);
            return this;
        }

        public Snake build() {
            return snake;
        }

    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public URI getProjectURI() {
        return projectURI;
    }

    public void setProjectURI(final URI projectURI) {
        this.projectURI = projectURI;
    }

    public SnakeInstance getSnakeInstance() {
        return snakeInstance;
    }

    public void setSnakeInstance(final SnakeInstance snakeInstance) {
        this.snakeInstance = snakeInstance;
    }
}
