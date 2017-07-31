/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.guice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

import spypunk.snake.Main;
import spypunk.snake.exception.SnakeException;
import spypunk.snake.model.Snake;
import spypunk.snake.service.SnakeService;
import spypunk.snake.service.SnakeServiceImpl;
import spypunk.snake.sound.cache.SoundClipCache;
import spypunk.snake.sound.cache.SoundClipCacheImpl;
import spypunk.snake.sound.service.SoundService;
import spypunk.snake.sound.service.SoundServiceImpl;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.cache.ImageCacheImpl;
import spypunk.snake.ui.controller.SnakeController;
import spypunk.snake.ui.controller.SnakeControllerImpl;
import spypunk.snake.ui.controller.event.SnakeControllerSnakeEventHandler;
import spypunk.snake.ui.controller.event.SnakeControllerSnakeEventHandlerImpl;
import spypunk.snake.ui.controller.gameloop.SnakeControllerGameLoop;
import spypunk.snake.ui.controller.gameloop.SnakeControllerGameLoopImpl;
import spypunk.snake.ui.controller.input.SnakeControllerInputHandler;
import spypunk.snake.ui.controller.input.SnakeControllerInputHandlerImpl;
import spypunk.snake.ui.factory.SnakeControllerCommandFactory;
import spypunk.snake.ui.factory.SnakeControllerCommandFactoryImpl;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.font.cache.FontCacheImpl;
import spypunk.snake.ui.view.SnakeView;
import spypunk.snake.ui.view.SnakeViewImpl;

public class SnakeModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String NAME_KEY = "name";

    private static final String VERSION_KEY = "version";

    private static final String URL_KEY = "url";

    private static final String SNAKE_PROPERTIES = "/snake.properties";

    private final Snake snake;

    public SnakeModule() {
        String name;
        String version;
        URI uri;

        try (InputStream inputStream = SnakeModule.class.getResource(SNAKE_PROPERTIES).openStream()) {
            final Properties properties = new Properties();

            properties.load(inputStream);

            name = properties.getProperty(NAME_KEY);
            version = properties.getProperty(VERSION_KEY);
            uri = URI.create(properties.getProperty(URL_KEY));
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SnakeException(e);
        }

        snake = new Snake(name, version, uri);
    }

    @Override
    protected void configure() {
        bind(SnakeService.class).to(SnakeServiceImpl.class);
        bind(SnakeController.class).to(SnakeControllerImpl.class);
        bind(ImageCache.class).to(ImageCacheImpl.class);
        bind(FontCache.class).to(FontCacheImpl.class);
        bind(SnakeControllerCommandFactory.class).to(SnakeControllerCommandFactoryImpl.class);
        bind(SoundService.class).to(SoundServiceImpl.class);
        bind(SoundClipCache.class).to(SoundClipCacheImpl.class);
        bind(SnakeControllerInputHandler.class).to(SnakeControllerInputHandlerImpl.class);
        bind(SnakeControllerSnakeEventHandler.class).to(SnakeControllerSnakeEventHandlerImpl.class);
        bind(SnakeControllerGameLoop.class).to(SnakeControllerGameLoopImpl.class);
        bind(SnakeView.class).to(SnakeViewImpl.class);
    }

    @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
    @BindingAnnotation
    public @interface SnakeProvider {
    }

    @Provides
    @SnakeProvider
    @Inject
    public Snake getSnake() {
        return snake;
    }
}
