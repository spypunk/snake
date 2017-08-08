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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import spypunk.snake.guice.SnakeModule.SnakeProvider;
import spypunk.snake.model.Food.Type;
import spypunk.snake.model.Snake;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.controller.SnakeController;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.icon.Icon;
import spypunk.snake.ui.util.SwingUtils;

@Singleton
public class SnakeMainViewImpl extends AbstractView implements SnakeMainView {

    private final JFrame frame;

    private final SnakeGridView snakeGridView;

    private final SnakeScoreView snakeScoreView;

    private final SnakeStatisticView snakeNormalStatisticView;

    private final SnakeStatisticView snakeBonusStatisticView;

    private final JLabel muteLabel;

    private final ImageIcon muteImageIcon;

    private final ImageIcon unmuteImageIcon;

    private final class SnakeViewWindowListener extends WindowAdapter {

        private final SnakeController snakeController;

        SnakeViewWindowListener(final SnakeController snakeController) {
            this.snakeController = snakeController;
        }

        @Override
        public void windowClosed(final WindowEvent e) {
            snakeController.onWindowClosed();
        }

        @Override
        public void windowOpened(final WindowEvent e) {
            snakeController.onWindowOpened();
        }
    }

    private static final class SnakeViewKeyAdapter extends KeyAdapter {

        private final SnakeController snakeController;

        SnakeViewKeyAdapter(final SnakeController snakeController) {
            this.snakeController = snakeController;
        }

        @Override
        public void keyPressed(final KeyEvent e) {
            snakeController.onKeyPressed(e.getKeyCode());
        }

        @Override
        public void keyReleased(final KeyEvent e) {
            snakeController.onKeyReleased(e.getKeyCode());
        }
    }

    private static final class URLLabelMouseAdapter extends MouseAdapter {

        private final SnakeController snakeController;
        private final JLabel urlLabel;

        URLLabelMouseAdapter(final SnakeController snakeController, final JLabel urlLabel) {
            this.snakeController = snakeController;
            this.urlLabel = urlLabel;
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
            snakeController.onURLOpen();
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            urlLabel.setForeground(Color.CYAN);
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            urlLabel.setForeground(DEFAULT_FONT_COLOR);
        }
    }

    @Inject
    public SnakeMainViewImpl(final SnakeController snakeController,
            final FontCache fontCache,
            final ImageCache imageCache,
            final @SnakeProvider Snake snake) {
        super(fontCache, imageCache, snake);

        snakeGridView = new SnakeGridView(fontCache, imageCache, snake);
        snakeScoreView = new SnakeScoreView(fontCache, imageCache, snake);

        snakeNormalStatisticView = new SnakeStatisticView(fontCache, imageCache, snake, Type.NORMAL);
        snakeBonusStatisticView = new SnakeStatisticView(fontCache, imageCache, snake, Type.BONUS);

        muteImageIcon = new ImageIcon(imageCache.getIcon(Icon.MUTE));
        unmuteImageIcon = new ImageIcon(imageCache.getIcon(Icon.UNMUTE));

        final URI projectURI = snake.getProjectURI();

        muteLabel = new JLabel(unmuteImageIcon);

        final JLabel urlLabel = new JLabel(projectURI.getHost() + projectURI.getPath());

        urlLabel.setFont(fontCache.getURLFont());
        urlLabel.setForeground(DEFAULT_FONT_COLOR);
        urlLabel.addMouseListener(new URLLabelMouseAdapter(snakeController, urlLabel));

        final JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.setBorder(BorderFactory.createEmptyBorder(CELL_SIZE, 3, 3, 3));
        bottomPanel.setBackground(Color.BLACK);

        final JPanel topPanel = new JPanel(new BorderLayout());

        final JPanel statisticsPanel = new JPanel(new BorderLayout(0, 3));

        statisticsPanel.add(snakeNormalStatisticView.getComponent(), BorderLayout.NORTH);
        statisticsPanel.add(snakeBonusStatisticView.getComponent(), BorderLayout.SOUTH);
        statisticsPanel.setBackground(Color.BLACK);

        topPanel.add(statisticsPanel, BorderLayout.WEST);
        topPanel.add(snakeScoreView.getComponent(), BorderLayout.CENTER);
        topPanel.setBackground(Color.BLACK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(CELL_SIZE / 2, CELL_SIZE, CELL_SIZE / 2, CELL_SIZE));

        bottomPanel.add(muteLabel, BorderLayout.WEST);
        bottomPanel.add(urlLabel, BorderLayout.EAST);

        final JPanel centerPanel = new JPanel(new BorderLayout(CELL_SIZE, 0));

        centerPanel.setBackground(Color.BLACK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, CELL_SIZE, 0, CELL_SIZE));
        centerPanel.add(snakeGridView.getComponent(), BorderLayout.CENTER);

        frame = new JFrame(snake.getName() + " " + snake.getVersion());

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(0, 0));
        frame.setResizable(false);
        frame.addWindowListener(new SnakeViewWindowListener(snakeController));
        frame.addKeyListener(new SnakeViewKeyAdapter(snakeController));
        frame.setIconImage(imageCache.getIcon(Icon.ICON));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();

        frame.setLocationRelativeTo(null);
    }

    @Override
    public void show() {
        update();

        SwingUtils.doInAWTThread(() -> frame.setVisible(true));
    }

    @Override
    public void update() {
        snakeGridView.update();
        snakeScoreView.update();
        snakeNormalStatisticView.update();
        snakeBonusStatisticView.update();
    }

    @Override
    public void setMuted(final boolean muted) {
        SwingUtils.doInAWTThread(() -> muteLabel.setIcon(muted ? muteImageIcon : unmuteImageIcon));
    }
}
