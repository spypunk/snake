/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.model;

import java.net.URI;
import java.util.List;

import com.google.common.collect.Lists;

public class Snake {

    private final String name;

    private final String version;

    private final URI projectURI;

    private final List<SnakeEvent> snakeEvents = Lists.newArrayList();

    private SnakeInstance snakeInstance;

    private State state = State.STOPPED;

    private boolean muted;

    public enum State {
        RUNNING {
            @Override
            public State onPause() {
                return PAUSED;
            }
        },
        PAUSED {
            @Override
            public State onPause() {
                return RUNNING;
            }
        },
        GAME_OVER,
        STOPPED;

        public State onPause() {
            return this;
        }
    }

    public Snake(final String name, final String version, final URI projectURI) {
        this.name = name;
        this.version = version;
        this.projectURI = projectURI;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public URI getProjectURI() {
        return projectURI;
    }

    public SnakeInstance getSnakeInstance() {
        return snakeInstance;
    }

    public void setSnakeInstance(final SnakeInstance snakeInstance) {
        this.snakeInstance = snakeInstance;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public List<SnakeEvent> getSnakeEvents() {
        return snakeEvents;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(final boolean muted) {
        this.muted = muted;
    }
}
