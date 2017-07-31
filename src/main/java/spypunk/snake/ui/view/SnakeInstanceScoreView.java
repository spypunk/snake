/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingConstants;

import spypunk.snake.guice.SnakeModule.SnakeProvider;
import spypunk.snake.model.Snake;
import spypunk.snake.model.SnakeInstance;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;

@Singleton
public class SnakeInstanceScoreView extends AbstractSnakeInstanceView {

    private static final String EMPTY_STRING = "";

    private final Rectangle scoreRectangle = new Rectangle(0, 0, 10 * CELL_SIZE, CELL_SIZE);

    @Inject
    public SnakeInstanceScoreView(final FontCache fontCache,
            final ImageCache imageCache,
            final @SnakeProvider Snake snake) {
        super(fontCache, imageCache, snake);

        initializeComponent(scoreRectangle.width, scoreRectangle.height);

        component.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void doUpdate(final Graphics2D graphics) {
        final SnakeInstance snakeInstance = snake.getSnakeInstance();

        final String score = snakeInstance == null ? EMPTY_STRING : String.valueOf(snakeInstance.getScore());

        SwingUtils.renderCenteredText(graphics, score, scoreRectangle, fontCache.getScoreFont(), DEFAULT_FONT_COLOR);
    }
}
