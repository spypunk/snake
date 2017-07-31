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

import spypunk.snake.guice.SnakeModule.SnakeProvider;
import spypunk.snake.model.Direction;
import spypunk.snake.model.Snake;
import spypunk.snake.model.Snake.State;
import spypunk.snake.service.SnakeService;
import spypunk.snake.sound.Sound;
import spypunk.snake.sound.service.SoundService;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;
import spypunk.snake.ui.view.SnakeView;

@Singleton
public class SnakeControllerCommandFactoryImpl implements SnakeControllerCommandFactory {

    private final SnakeService snakeService;

    private final SoundService soundService;

    private final Snake snake;

    private final SnakeView snakeView;

    @Inject
    public SnakeControllerCommandFactoryImpl(final SnakeService snakeService,
            final SoundService soundService,
            final @SnakeProvider Snake snake,
            final SnakeView snakeView) {
        this.snakeService = snakeService;
        this.soundService = soundService;
        this.snake = snake;
        this.snakeView = snakeView;
    }

    @Override
    public SnakeControllerCommand createNewGameCommand() {
        return () -> {
            snakeService.start();
            soundService.playMusic(Sound.BACKGROUND);
        };
    }

    @Override
    public SnakeControllerCommand createPauseCommand() {
        return () -> {
            snakeService.pause();

            final State state = snake.getState();

            if (State.PAUSED.equals(state)) {
                soundService.pauseMusic();
            } else if (State.RUNNING.equals(state)) {
                soundService.resumeMusic();
            }
        };
    }

    @Override
    public SnakeControllerCommand createDirectionCommand(final Direction direction) {
        return () -> snakeService.updateDirection(direction);
    }

    @Override
    public SnakeControllerCommand createMuteCommand() {
        return () -> {
            snakeService.mute();

            final boolean muted = snake.isMuted();

            snakeView.setMuted(muted);
            soundService.setMuted(muted);
        };
    }

    @Override
    public SnakeControllerCommand createIncreaseVolumeCommand() {
        return soundService::increaseVolume;
    }

    @Override
    public SnakeControllerCommand createDecreaseVolumeCommand() {
        return soundService::decreaseVolume;
    }

    @Override
    public SnakeControllerCommand createGameOverCommand() {
        return () -> soundService.playMusic(Sound.GAME_OVER);
    }

    @Override
    public SnakeControllerCommand createFoodEatenCommand() {
        return () -> soundService.playSound(Sound.FOOD_EATEN);
    }
}
