/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller.command.cache;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Maps;

import spypunk.snake.guice.SnakeModule.SnakeProvider;
import spypunk.snake.model.Direction;
import spypunk.snake.model.Snake;
import spypunk.snake.model.Snake.State;
import spypunk.snake.service.SnakeService;
import spypunk.snake.sound.Sound;
import spypunk.snake.sound.service.SoundService;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.view.SnakeMainView;

@Singleton
public class SnakeControllerCommandCacheImpl implements SnakeControllerCommandCache {

    private final SnakeService snakeService;

    private final SoundService soundService;

    private final Snake snake;

    private final SnakeMainView snakeMainView;

    private final Map<SnakeControllerCommandType, SnakeControllerCommand> snakeControllerCommands = Maps
            .newHashMap();

    @Inject
    public SnakeControllerCommandCacheImpl(final SnakeService snakeService,
            final SoundService soundService,
            final @SnakeProvider Snake snake,
            final SnakeMainView snakeMainView) {
        this.snakeService = snakeService;
        this.soundService = soundService;
        this.snake = snake;
        this.snakeMainView = snakeMainView;

        snakeControllerCommands.put(SnakeControllerCommandType.DOWN, createDirectionCommand(Direction.DOWN));
        snakeControllerCommands.put(SnakeControllerCommandType.LEFT, createDirectionCommand(Direction.LEFT));
        snakeControllerCommands.put(SnakeControllerCommandType.RIGHT, createDirectionCommand(Direction.RIGHT));
        snakeControllerCommands.put(SnakeControllerCommandType.UP, createDirectionCommand(Direction.UP));
        snakeControllerCommands.put(SnakeControllerCommandType.DECREASE_VOLUME, createDecreaseVolumeCommand());
        snakeControllerCommands.put(SnakeControllerCommandType.INCREASE_VOLUME, createIncreaseVolumeCommand());
        snakeControllerCommands.put(SnakeControllerCommandType.MUTE, createMuteCommand());
        snakeControllerCommands.put(SnakeControllerCommandType.NEW_GAME, createNewGameCommand());
        snakeControllerCommands.put(SnakeControllerCommandType.OPEN_PROJECT_URL, createOpenProjectURLCommand());
        snakeControllerCommands.put(SnakeControllerCommandType.PAUSE, createPauseCommand());
        snakeControllerCommands.put(SnakeControllerCommandType.GAME_OVER, createGameOverCommand());
        snakeControllerCommands.put(SnakeControllerCommandType.FOOD_EATEN, createFoodEatenCommand());
    }

    @Override
    public SnakeControllerCommand getSnakeControllerCommand(
            final SnakeControllerCommandType snakeControllerCommandType) {

        return snakeControllerCommands.get(snakeControllerCommandType);
    }

    private SnakeControllerCommand createNewGameCommand() {
        return () -> {
            snakeService.start();
            soundService.playMusic(Sound.BACKGROUND);
        };
    }

    private SnakeControllerCommand createPauseCommand() {
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

    private SnakeControllerCommand createDirectionCommand(final Direction direction) {
        return () -> snakeService.updateDirection(direction);
    }

    private SnakeControllerCommand createMuteCommand() {
        return () -> {
            snakeService.mute();

            final boolean muted = snake.isMuted();

            snakeMainView.setMuted(muted);
            soundService.setMuted(muted);
        };
    }

    private SnakeControllerCommand createIncreaseVolumeCommand() {
        return soundService::increaseVolume;
    }

    private SnakeControllerCommand createDecreaseVolumeCommand() {
        return soundService::decreaseVolume;
    }

    private SnakeControllerCommand createGameOverCommand() {
        return () -> soundService.playMusic(Sound.GAME_OVER);
    }

    private SnakeControllerCommand createFoodEatenCommand() {
        return () -> soundService.playSound(Sound.FOOD_EATEN);
    }

    private SnakeControllerCommand createOpenProjectURLCommand() {
        return () -> SwingUtils.openURI(snake.getProjectURI());
    }
}
