/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.cache;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spypunk.snake.exception.SnakeException;
import spypunk.snake.model.Food.Type;
import spypunk.snake.ui.icon.Icon;
import spypunk.snake.ui.snakepart.SnakePart;

@Singleton
public class ImageCacheImpl implements ImageCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCacheImpl.class);

    private static final String IMAGE_FILE_PATTERN = "%s%s.png";

    private static final String ICONS_FOLDER = "/img/icons/";

    private static final String SNAKE_FOLDER = "/img/snake/";

    private static final String FOOD_FOLDER = "/img/food/";

    private final Map<Icon, Image> icons = createIcons();

    private final Map<SnakePart, Image> snakePartImages = createSnakePartImages();

    private final Map<Type, Image> foodImages = createFoodImages();

    @Override
    public Image getIcon(final Icon icon) {
        return icons.get(icon);
    }

    @Override
    public Image getSnakePartImage(final SnakePart snakePart) {
        return snakePartImages.get(snakePart);
    }

    @Override
    public Image getFoodImage(final Type foodType) {
        return foodImages.get(foodType);
    }

    private static Image createIcon(final Icon icon) {
        return createImage(ICONS_FOLDER, icon.name().toLowerCase());
    }

    private static Image createSnakePartImage(final SnakePart snakePart) {
        return createImage(SNAKE_FOLDER, snakePart.name().toLowerCase());
    }

    private static Image createFoodImage(final Type foodType) {
        return createImage(FOOD_FOLDER, foodType.name().toLowerCase());
    }

    private static Image createImage(final String imageFolder, final String fileName) {
        final String resourceName = String.format(IMAGE_FILE_PATTERN, imageFolder, fileName);

        try (InputStream inputStream = ImageCacheImpl.class.getResourceAsStream(resourceName)) {
            return ImageIO.read(inputStream);
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SnakeException(e);
        }
    }

    private static Map<Icon, Image> createIcons() {
        return Arrays.asList(Icon.values()).stream().collect(Collectors.toMap(Function.identity(),
            ImageCacheImpl::createIcon));
    }

    private static Map<SnakePart, Image> createSnakePartImages() {
        return Arrays.asList(SnakePart.values()).stream().collect(Collectors.toMap(Function.identity(),
            ImageCacheImpl::createSnakePartImage));
    }

    private static Map<Type, Image> createFoodImages() {
        return Arrays.asList(Type.values()).stream().collect(Collectors.toMap(Function.identity(),
            ImageCacheImpl::createFoodImage));
    }
}
