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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import spypunk.snake.model.Snake;
import spypunk.snake.model.SnakeInstance;
import spypunk.snake.ui.font.FontType;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;

public class SnakeInstanceScoreView extends AbstractSnakeInstanceView {

    private static final String EMPTY_STRING = "".intern();

    private static final long serialVersionUID = 3093168306699870331L;

    private final Rectangle scoreRectangle = new Rectangle(0, 0, 10 * CELL_SIZE, CELL_SIZE);

    private final Font scoreFont;

    private final Snake snake;

    public SnakeInstanceScoreView(final FontCache fontCache,
            final Snake snake) {
        this.snake = snake;

        scoreFont = fontCache.getFont(FontType.SCORE);

        image = new BufferedImage(scoreRectangle.width, scoreRectangle.height,
                BufferedImage.TYPE_INT_ARGB);

        setHorizontalAlignment(SwingConstants.CENTER);
        setIcon(new ImageIcon(image));
        setIgnoreRepaint(true);
    }

    @Override
    public void update() {
        SwingUtils.doInGraphics(image, this::renderScore);
        repaint();
    }

    private void renderScore(final Graphics2D graphics) {
        final SnakeInstance snakeInstance = snake.getSnakeInstance();

        final String score = snakeInstance == null ? EMPTY_STRING : String.valueOf(snakeInstance.getScore());

        SwingUtils.renderCenteredText(graphics, score, scoreRectangle, scoreFont, DEFAULT_FONT_COLOR);
    }
}
