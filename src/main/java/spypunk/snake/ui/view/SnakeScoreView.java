/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.view;

import static spypunk.snake.ui.constants.SnakeUIConstants.CELL_SIZE;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import spypunk.snake.model.Snake;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.util.SwingUtils.Text;

public class SnakeScoreView extends AbstractSnakeView {

    private final Rectangle scoreRectangle = new Rectangle(0, 0, 30 * CELL_SIZE, 2 * CELL_SIZE);

    public SnakeScoreView(final FontCache fontCache,
            final ImageCache imageCache,
            final Snake snake) {
        super(fontCache, imageCache, snake);

        initializeComponent(scoreRectangle.width, scoreRectangle.height);
    }

    @Override
    protected void doPaint(final Graphics2D graphics) {
        final String score = String.valueOf(snake.getScore());
        final Text scoreText = new Text(score, fontCache.getScoreFont());

        SwingUtils.renderCenteredText(graphics, scoreRectangle, scoreText);
    }
}
