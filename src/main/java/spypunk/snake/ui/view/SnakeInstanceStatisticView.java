/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.view;

import static spypunk.snake.ui.constants.SnakeUIConstants.CELL_SIZE;
import static spypunk.snake.ui.constants.SnakeUIConstants.DEFAULT_FONT_COLOR;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import spypunk.snake.model.Food.Type;
import spypunk.snake.model.Snake;
import spypunk.snake.model.SnakeInstance;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;

public class SnakeInstanceStatisticView extends AbstractSnakeInstanceView {

    private final Rectangle scoreRectangle = new Rectangle(CELL_SIZE, 0, 4 * CELL_SIZE, CELL_SIZE);

    private final Rectangle foodRectangle = new Rectangle(0, 0, CELL_SIZE, CELL_SIZE);

    private final Type foodType;

    public SnakeInstanceStatisticView(final FontCache fontCache,
            final ImageCache imageCache,
            final Snake snake,
            final Type foodType) {
        super(fontCache, imageCache, snake);

        this.foodType = foodType;

        initializeComponent(foodRectangle.width + scoreRectangle.width, CELL_SIZE);
    }

    @Override
    protected void doUpdate(final Graphics2D graphics) {
        final SnakeInstance snakeInstance = snake.getSnakeInstance();

        final String count = snakeInstance == null ? "0" : String.valueOf(snakeInstance.getStatistics().get(foodType));

        SwingUtils.renderCenteredText(graphics, count, scoreRectangle, fontCache.getDefaultFont(), DEFAULT_FONT_COLOR);

        SwingUtils.drawImage(graphics, imageCache.getFoodImage(foodType), foodRectangle);
    }
}
