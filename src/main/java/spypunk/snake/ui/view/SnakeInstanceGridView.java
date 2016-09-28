/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.view;

import static spypunk.snake.ui.constants.SnakeUIConstants.CELL_SIZE;
import static spypunk.snake.ui.constants.SnakeUIConstants.DEFAULT_BORDER_COLOR;
import static spypunk.snake.ui.constants.SnakeUIConstants.DEFAULT_FONT_COLOR;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.google.common.collect.Maps;

import spypunk.snake.constants.SnakeConstants;
import spypunk.snake.model.Direction;
import spypunk.snake.model.Food;
import spypunk.snake.model.Snake;
import spypunk.snake.model.SnakeInstance;
import spypunk.snake.model.SnakeInstance.State;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.FontType;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.snakepart.SnakePart;
import spypunk.snake.ui.util.SwingUtils;

public class SnakeInstanceGridView extends AbstractSnakeInstanceView {

    private static final long serialVersionUID = -3487901883637598196L;

    private static final String PAUSE = "PAUSE".intern();

    private static final Color SNAKE_FROZEN_FG_COLOR = new Color(30, 30, 30, 200);

    private static final String GAME_OVER = "GAME OVER".intern();

    private static final String PRESS_SPACE = "PRESS SPACE".intern();

    private final Rectangle gridRectangle;

    private final Rectangle frozenGridRectangle;

    private final ImageCache imageCache;

    private final Font frozenFont;

    private final int x;

    private final int y;

    private final Map<Point, Rectangle> rectanglesCache = Maps.newHashMap();

    public SnakeInstanceGridView(final FontCache fontCache,
            final ImageCache imageCache,
            final Snake snake) {
        this.imageCache = imageCache;
        this.snake = snake;

        frozenFont = fontCache.getFont(FontType.FROZEN);

        gridRectangle = new Rectangle(0, 0, SnakeConstants.WIDTH * CELL_SIZE + 1,
                SnakeConstants.HEIGHT * CELL_SIZE + 1);

        frozenGridRectangle = new Rectangle(gridRectangle.x + 1, gridRectangle.y + 1, gridRectangle.width - 1,
                gridRectangle.height - 1);

        x = gridRectangle.x + 1;
        y = gridRectangle.y + 1;

        image = new BufferedImage(gridRectangle.width + 1, gridRectangle.height + 1,
                BufferedImage.TYPE_INT_ARGB);

        setIcon(new ImageIcon(image));
        setIgnoreRepaint(true);
    }

    @Override
    public void update() {
        SwingUtils.doInGraphics(image, this::renderSnake);
        repaint();
    }

    private void renderSnake(final Graphics2D graphics) {
        final SnakeInstance snakeInstance = snake.getSnakeInstance();

        graphics.setColor(DEFAULT_BORDER_COLOR);

        graphics.drawRect(gridRectangle.x, gridRectangle.y, gridRectangle.width,
            gridRectangle.height);

        if (snakeInstance == null) {
            renderSnakeNew(graphics);
            return;
        }

        final List<Point> snakeParts = snakeInstance.getSnakeParts();

        for (int i = 0; i < snakeParts.size(); ++i) {
            final SnakePart snakePart = getSnakePart(snakeInstance, i);
            renderSnakePart(graphics, snakeParts.get(i), snakePart);
        }

        renderFood(graphics, snakeInstance);

        final State state = snakeInstance.getState();

        if (!State.RUNNING.equals(state)) {
            renderSnakeFrozen(graphics, state);
        }
    }

    private SnakePart getSnakePart(final SnakeInstance snakeInstance, final int i) {
        final Direction snakeDirection = snakeInstance.getSnakeDirection();

        if (i == 0) {
            return getSnakeHeadPart(snakeDirection);
        }

        final List<Point> snakeParts = snakeInstance.getSnakeParts();
        final Point nextSnakePartLocation = i < snakeParts.size() - 1 ? snakeParts.get(i + 1) : null;

        if (nextSnakePartLocation == null) {
            return SnakePart.TAIL;
        }

        final Point snakePartLocation = snakeParts.get(i);
        final Point previousSnakePartLocation = snakeParts.get(i - 1);

        if (previousSnakePartLocation.x == nextSnakePartLocation.x) {
            return SnakePart.VERTICAL;
        }

        if (previousSnakePartLocation.y == nextSnakePartLocation.y) {
            return SnakePart.HORIZONTAL;
        }

        return getSnakeCornerPart(snakePartLocation, nextSnakePartLocation, previousSnakePartLocation);
    }

    private SnakePart getSnakeCornerPart(final Point snakePartLocation, final Point nextSnakePartLocation,
            final Point previousSnakePartLocation) {
        final boolean previousXLesser = previousSnakePartLocation.x < snakePartLocation.x;
        final boolean nextYLesser = nextSnakePartLocation.y < snakePartLocation.y;
        final boolean nextXLesser = nextSnakePartLocation.x < snakePartLocation.x;
        final boolean previousYLesser = previousSnakePartLocation.y < snakePartLocation.y;
        final boolean nextYGreater = nextSnakePartLocation.y > snakePartLocation.y;
        final boolean previousYGreater = previousSnakePartLocation.y > snakePartLocation.y;
        final boolean nextXGreater = nextSnakePartLocation.x > snakePartLocation.x;
        final boolean previousXGreater = previousSnakePartLocation.x > snakePartLocation.x;

        if (previousXLesser && nextYLesser
                || nextXLesser && previousYLesser) {
            return SnakePart.BOTTOM_RIGHT;
        }

        if (previousXLesser && nextYGreater
                || nextXLesser && previousYGreater) {
            return SnakePart.TOP_RIGHT;
        }

        if (previousYLesser && nextXGreater
                || nextYLesser && previousXGreater) {
            return SnakePart.BOTTOM_LEFT;
        }

        return SnakePart.TOP_LEFT;
    }

    private SnakePart getSnakeHeadPart(final Direction snakeDirection) {
        switch (snakeDirection) {
        case DOWN:
            return SnakePart.HEAD_BOTTOM;

        case UP:
            return SnakePart.HEAD_TOP;

        case LEFT:
            return SnakePart.HEAD_LEFT;

        default:
            return SnakePart.HEAD_RIGHT;
        }
    }

    private void renderFood(final Graphics2D graphics, final SnakeInstance snakeInstance) {
        final Food food = snakeInstance.getFood();
        final Image foodImage = imageCache.getFoodImage(food.getType());
        final Rectangle rectangle = getRectangle(food.getLocation());

        SwingUtils.drawImage(graphics, foodImage, rectangle);
    }

    private void renderSnakePart(final Graphics2D graphics, final Point location, final SnakePart snakePart) {
        final Image snakeImage = imageCache.getSnakeImage(snakePart);
        final Rectangle rectangle = getRectangle(location);

        SwingUtils.drawImage(graphics, snakeImage, rectangle);
    }

    private Rectangle getRectangle(final Point location) {
        Rectangle rectangle;

        if (!rectanglesCache.containsKey(location)) {
            final int x1 = x + location.x * CELL_SIZE;
            final int y1 = y + location.y * CELL_SIZE;

            rectangle = new Rectangle(x1, y1, CELL_SIZE, CELL_SIZE);

            rectanglesCache.put(location, rectangle);
        } else {
            rectangle = rectanglesCache.get(location);
        }

        return rectangle;
    }

    private void renderSnakeNew(final Graphics2D graphics) {
        SwingUtils.renderCenteredText(graphics, PRESS_SPACE, gridRectangle,
            frozenFont, DEFAULT_FONT_COLOR);
    }

    private void renderSnakeFrozen(final Graphics2D graphics, final State state) {
        graphics.setColor(SNAKE_FROZEN_FG_COLOR);
        graphics.fillRect(frozenGridRectangle.x, frozenGridRectangle.y, frozenGridRectangle.width,
            frozenGridRectangle.height);

        SwingUtils.renderCenteredText(graphics, State.GAME_OVER.equals(state) ? GAME_OVER : PAUSE, gridRectangle,
            frozenFont, DEFAULT_FONT_COLOR);
    }
}
