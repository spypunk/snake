/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.view;

import static spypunk.snake.ui.constants.SnakeUIConstants.CELL_SIZE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import spypunk.snake.constants.SnakeConstants;
import spypunk.snake.model.Direction;
import spypunk.snake.model.Food;
import spypunk.snake.model.Snake;
import spypunk.snake.model.Snake.State;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.snakepart.SnakePart;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.util.SwingUtils.Text;

public class SnakeGridView extends AbstractSnakeView {

    private static final String PAUSE = "PAUSE";

    private static final Color NOT_RUNNING_FG_COLOR = new Color(30, 30, 30, 200);

    private static final String GAME_OVER = "GAME OVER";

    private static final String PRESS_SPACE = "PRESS SPACE";

    private static final Map<Direction, SnakePart> SNAKE_HEAD_PARTS = ImmutableMap.of(Direction.DOWN,
        SnakePart.HEAD_BOTTOM, Direction.LEFT, SnakePart.HEAD_LEFT,
        Direction.RIGHT, SnakePart.HEAD_RIGHT, Direction.UP, SnakePart.HEAD_TOP);

    private final Rectangle gridRectangle;

    private final Text snakeStoppedText;

    private final Text snakeGameOverText;

    private final Text snakePausedText;

    public SnakeGridView(final FontCache fontCache,
            final ImageCache imageCache,
            final Snake snake) {
        super(fontCache, imageCache, snake);

        gridRectangle = new Rectangle(0, 0, SnakeConstants.WIDTH * CELL_SIZE,
                SnakeConstants.HEIGHT * CELL_SIZE);

        snakeStoppedText = new Text(PRESS_SPACE, fontCache.getBiggerFont());
        snakeGameOverText = new Text(GAME_OVER, fontCache.getBiggerFont());
        snakePausedText = new Text(PAUSE, fontCache.getBiggerFont());

        initializeComponent(gridRectangle.width, gridRectangle.height, true);
    }

    @Override
    protected void doUpdate(final Graphics2D graphics) {
        final State state = snake.getState();

        if (State.STOPPED.equals(state)) {
            renderSnakeStopped(graphics);
            return;
        }

        final List<Point> snakePartLocations = snake.getSnakePartLocations();

        for (int i = 0; i < snakePartLocations.size(); ++i) {
            renderSnakePart(graphics, snakePartLocations, i);
        }

        renderFood(graphics);

        if (!State.RUNNING.equals(state)) {
            renderSnakeNotRunning(graphics, state);
        }
    }

    private SnakePart getSnakePart(final List<Point> snakePartLocations, final int i) {
        final Direction direction = snake.getDirection();

        if (i == 0) {
            return SNAKE_HEAD_PARTS.get(direction);
        }

        if (i == snakePartLocations.size() - 1) {
            return SnakePart.TAIL;
        }

        final Point nextSnakePartLocation = snakePartLocations.get(i + 1);
        final Point previousSnakePartLocation = snakePartLocations.get(i - 1);

        if (previousSnakePartLocation.x == nextSnakePartLocation.x) {
            return SnakePart.VERTICAL;
        }

        if (previousSnakePartLocation.y == nextSnakePartLocation.y) {
            return SnakePart.HORIZONTAL;
        }

        return getSnakeCornerPart(snakePartLocations.get(i), nextSnakePartLocation, previousSnakePartLocation);
    }

    private SnakePart getSnakeCornerPart(final Point snakePartLocation, final Point nextSnakePartLocation,
            final Point previousSnakePartLocation) {

        final boolean previousXLess = previousSnakePartLocation.x < snakePartLocation.x;
        final boolean nextYLess = nextSnakePartLocation.y < snakePartLocation.y;
        final boolean nextXLess = nextSnakePartLocation.x < snakePartLocation.x;
        final boolean previousYLess = previousSnakePartLocation.y < snakePartLocation.y;

        if (previousXLess && nextYLess
                || nextXLess && previousYLess) {
            return SnakePart.BOTTOM_RIGHT;
        }

        final boolean nextYGreater = nextSnakePartLocation.y > snakePartLocation.y;
        final boolean previousYGreater = previousSnakePartLocation.y > snakePartLocation.y;

        if (previousXLess && nextYGreater
                || nextXLess && previousYGreater) {
            return SnakePart.TOP_RIGHT;
        }

        final boolean nextXGreater = nextSnakePartLocation.x > snakePartLocation.x;
        final boolean previousXGreater = previousSnakePartLocation.x > snakePartLocation.x;

        if (previousYLess && nextXGreater
                || nextYLess && previousXGreater) {
            return SnakePart.BOTTOM_LEFT;
        }

        return SnakePart.TOP_LEFT;
    }

    private void renderFood(final Graphics2D graphics) {
        final Food food = snake.getFood();
        final Image foodImage = imageCache.getFoodImage(food.getType());
        final Rectangle rectangle = getRectangle(food.getLocation());

        SwingUtils.drawImage(graphics, foodImage, rectangle);
    }

    private void renderSnakePart(final Graphics2D graphics, final List<Point> snakePartLocations, final int i) {
        final SnakePart snakePart = getSnakePart(snakePartLocations, i);
        final Image snakePartImage = imageCache.getSnakePartImage(snakePart);
        final Rectangle snakePartRectangle = getRectangle(snakePartLocations.get(i));

        SwingUtils.drawImage(graphics, snakePartImage, snakePartRectangle);
    }

    private Rectangle getRectangle(final Point location) {
        final int x1 = location.x * CELL_SIZE;
        final int y1 = location.y * CELL_SIZE;

        return new Rectangle(x1, y1, CELL_SIZE, CELL_SIZE);
    }

    private void renderSnakeStopped(final Graphics2D graphics) {
        SwingUtils.renderCenteredText(graphics, gridRectangle, snakeStoppedText);
    }

    private void renderSnakeNotRunning(final Graphics2D graphics, final State state) {
        graphics.setColor(NOT_RUNNING_FG_COLOR);
        graphics.fillRect(gridRectangle.x, gridRectangle.y, gridRectangle.width,
            gridRectangle.height);

        SwingUtils.renderCenteredText(graphics, gridRectangle,
            State.GAME_OVER.equals(state) ? snakeGameOverText : snakePausedText);
    }
}
