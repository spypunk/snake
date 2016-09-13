/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.factory;

import javax.inject.Inject;
import javax.inject.Singleton;

import spypunk.snake.model.Direction;
import spypunk.snake.model.SnakeInstance;
import spypunk.snake.model.SnakeInstance.State;
import spypunk.snake.service.SnakeInstanceService;
import spypunk.snake.sound.Sound;
import spypunk.snake.sound.service.SoundService;
import spypunk.snake.ui.controller.SnakeController;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;

@Singleton
public class SnakeControllerCommandFactoryImpl implements SnakeControllerCommandFactory {

    private final SnakeInstanceService snakeInstanceService;

    private final SoundService soundService;

    private final SnakeController snakeController;

    @Inject
    public SnakeControllerCommandFactoryImpl(final SnakeInstanceService snakeInstanceService,
            final SoundService soundService,
            final SnakeController snakeController) {
        this.snakeInstanceService = snakeInstanceService;
        this.soundService = soundService;
        this.snakeController = snakeController;
    }

    @Override
    public SnakeControllerCommand createNewGameSnakeControllerCommand() {
        return snake -> {
            snakeInstanceService.create(snake);

            soundService.playMusic(Sound.BACKGROUND);
        };
    }

    @Override
    public SnakeControllerCommand createPauseSnakeControllerCommand() {
        return snake -> {
            final SnakeInstance snakeInstance = snake.getSnakeInstance();

            if (snakeInstance != null) {
                snakeInstanceService.pause(snakeInstance);

                final State state = snakeInstance.getState();

                if (State.RUNNING.equals(state) || State.PAUSED.equals(state)) {
                    soundService.pauseMusic();
                }
            }
        };
    }

    @Override
    public SnakeControllerCommand createDirectionSnakeControllerCommand(final Direction direction) {
        return snake -> {
            final SnakeInstance snakeInstance = snake.getSnakeInstance();

            if (snakeInstance != null) {
                snakeInstanceService.updateDirection(snake.getSnakeInstance(), direction);
            }
        };
    }

    @Override
    public SnakeControllerCommand createMuteSnakeControllerCommand() {
        return snake -> {
            soundService.mute();
            snakeController.getSnakeView().setMute(soundService.isMute());
        };
    }

    @Override
    public SnakeControllerCommand createIncreaseVolumeSnakeControllerCommand() {
        return snake -> soundService.increaseVolume();
    }

    @Override
    public SnakeControllerCommand createDecreaseVolumeSnakeControllerCommand() {
        return snake -> soundService.decreaseVolume();
    }

    @Override
    public SnakeControllerCommand createGameOverSnakeControllerCommand() {
        return snake -> soundService.playMusic(Sound.GAME_OVER);
    }

    @Override
    public SnakeControllerCommand createFoodEatenSnakeControllerCommand() {
        return snake -> soundService.playSound(Sound.FOOD_EATEN);
    }
}
