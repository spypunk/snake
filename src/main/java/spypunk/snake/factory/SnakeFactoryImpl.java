/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.factory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spypunk.snake.Main;
import spypunk.snake.exception.SnakeException;
import spypunk.snake.model.Snake;

@Singleton
public class SnakeFactoryImpl implements SnakeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String NAME_KEY = "name".intern();

    private static final String VERSION_KEY = "version".intern();

    private static final String URL_KEY = "url".intern();

    private static final String SNAKE_PROPERTIES = "/snake.properties".intern();

    private final String name;

    private final String version;

    private final URI uri;

    public SnakeFactoryImpl() {
        try (InputStream inputStream = SnakeFactoryImpl.class.getResource(SNAKE_PROPERTIES).openStream()) {
            final Properties properties = new Properties();

            properties.load(inputStream);

            name = properties.getProperty(NAME_KEY);
            version = properties.getProperty(VERSION_KEY);
            uri = URI.create(properties.getProperty(URL_KEY));
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SnakeException(e);
        }
    }

    @Override
    public Snake createSnake() {
        return Snake.Builder.instance().setName(name).setVersion(version).setProjectURI(uri).build();
    }
}
