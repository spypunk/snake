/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.util;

import static spypunk.snake.ui.constants.SnakeUIConstants.DEFAULT_FONT_COLOR;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spypunk.snake.exception.SnakeException;

public final class SwingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwingUtils.class);

    public static class Text {

        private final String value;

        private final Font font;

        private final Color fontColor;

        public Text(final String value, final Font font) {
            this.value = value;
            this.font = font;
            fontColor = DEFAULT_FONT_COLOR;
        }
    }

    private SwingUtils() {
        throw new IllegalAccessError();
    }

    public static void doInAWTThread(final Runnable runnable) {
        doInAWTThread(runnable, false);
    }

    public static void doInAWTThread(final Runnable runnable, final boolean wait) {
        if (wait) {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (final InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
                throw new SnakeException(e);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error(e.getMessage(), e);
                throw new SnakeException(e);
            }
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    private static Rectangle getCenteredTextRectangle(final Graphics2D graphics, final Rectangle rectangle,
            final Text text) {
        final Rectangle2D textBounds = getTextBounds(graphics, text);

        final int x1 = (int) (rectangle.x + (rectangle.width - textBounds.getWidth()) / 2);
        final int y1 = (int) (rectangle.y + (rectangle.height + textBounds.getHeight()) / 2);

        return new Rectangle(x1, y1, (int) textBounds.getWidth(), (int) textBounds.getHeight());
    }

    private static Rectangle2D getTextBounds(final Graphics2D graphics, final Text text) {
        final FontRenderContext frc = graphics.getFontRenderContext();
        final GlyphVector gv = text.font.createGlyphVector(frc, text.value);

        return gv.getVisualBounds();
    }

    public static void drawImage(final Graphics2D graphics, final Image image,
            final Rectangle rectangle) {
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        graphics.drawImage(image, rectangle.x, rectangle.y, rectangle.x + rectangle.width,
            rectangle.y + rectangle.height, 0, 0, imageWidth, imageHeight,
            null);
    }

    public static void openURI(final URI uri) {
        final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (final IOException e) {
                LOGGER.warn("Cannot open following URL : " + uri + " | " + e.getMessage(), e);
            }
        } else {
            final String message = String
                    .format("Your system does not support URL browsing, cannot open following URL : %s", uri);
            LOGGER.warn(message);
        }
    }

    public static void renderCenteredText(final Graphics2D graphics, final Rectangle rectangle,
            final Text text) {
        graphics.setFont(text.font);
        graphics.setColor(text.fontColor);

        final Rectangle textRectangle = SwingUtils.getCenteredTextRectangle(graphics, rectangle, text);

        graphics.drawString(text.value, textRectangle.x, textRectangle.y);
    }

    public static void doInGraphics(final BufferedImage image, final Consumer<Graphics2D> consumer) {
        final Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        consumer.accept(graphics);

        graphics.dispose();
    }
}
