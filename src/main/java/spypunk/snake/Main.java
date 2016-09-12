/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake;

import java.io.File;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.ConfigurationException;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import spypunk.snake.guice.SnakeModule;
import spypunk.snake.ui.controller.SnakeController;
import spypunk.snake.ui.util.SwingUtils;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String USER_HOME = System.getProperty("user.home").intern();

    private static final String ERROR_TITLE = "Error".intern();

    private static final String ERROR_MESSAGE_TEMPLATE = "An error occurred, check the log file %s%s.spypunk-snake%ssnake.log for more information"
            .intern();

    private static final String ERROR_MESSAGE = String
            .format(ERROR_MESSAGE_TEMPLATE,
                USER_HOME, File.separator, File.separator)
            .intern();

    private Main() {
        throw new IllegalAccessError();
    }

    public static void main(final String[] args) {
        try {
            final Injector injector = Guice.createInjector(new SnakeModule());
            injector.getInstance(SnakeController.class).start();
        } catch (CreationException | ConfigurationException | ProvisionException e) {
            LOGGER.error(e.getMessage(), e);
            SwingUtils.doInAWTThread(Main::showErrorDialog, false);
        }
    }

    private static void showErrorDialog() {
        JOptionPane.showMessageDialog(null,
            ERROR_MESSAGE,
            ERROR_TITLE,
            JOptionPane.ERROR_MESSAGE);
    }
}
