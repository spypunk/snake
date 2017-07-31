/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.view;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import spypunk.snake.model.Snake;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;

public abstract class AbstractSnakeInstanceView extends AbstractView {

    protected JLabel component;

    protected AbstractSnakeInstanceView(final FontCache fontCache, final ImageCache imageCache, final Snake snake) {
        super(fontCache, imageCache, snake);
    }

    protected void initializeComponent(final int width, final int height) {
        component = new JLabel();

        final BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);

        component.setIcon(new ImageIcon(image));
        component.setIgnoreRepaint(true);
    }

    @Override
    public void update() {
        final BufferedImage image = (BufferedImage) ((ImageIcon) component.getIcon()).getImage();

        SwingUtils.doInGraphics(image, this::doUpdate);

        component.repaint();
    }

    public Component getComponent() {
        return component;
    }

    protected abstract void doUpdate(final Graphics2D graphics);
}
