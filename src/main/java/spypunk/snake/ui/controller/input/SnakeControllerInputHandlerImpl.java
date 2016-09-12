/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.controller.input;

import java.awt.event.KeyEvent;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.collections4.ListUtils;

import com.google.common.collect.Maps;

import spypunk.snake.model.Direction;
import spypunk.snake.ui.controller.command.SnakeControllerCommand;
import spypunk.snake.ui.factory.SnakeControllerCommandFactory;

@Singleton
public class SnakeControllerInputHandlerImpl implements SnakeControllerInputHandler {

    private final BitSet pressedKeysBitSet = new BitSet();

    private final BitSet releasedKeysBitSet = new BitSet();

    private final Map<Integer, Supplier<SnakeControllerCommand>> pressedKeyCodesHandlers = Maps.newHashMap();

    private final Map<Integer, Supplier<SnakeControllerCommand>> releasedKeyCodesHandlers = Maps.newHashMap();

    @Inject
    public SnakeControllerInputHandlerImpl(final SnakeControllerCommandFactory snakeControllerCommandFactory) {
        pressedKeyCodesHandlers.put(KeyEvent.VK_LEFT,
            () -> snakeControllerCommandFactory.createDirectionSnakeControllerCommand(Direction.LEFT));

        pressedKeyCodesHandlers.put(KeyEvent.VK_RIGHT,
            () -> snakeControllerCommandFactory.createDirectionSnakeControllerCommand(Direction.RIGHT));

        pressedKeyCodesHandlers.put(KeyEvent.VK_DOWN,
            () -> snakeControllerCommandFactory.createDirectionSnakeControllerCommand(Direction.DOWN));

        releasedKeyCodesHandlers.put(KeyEvent.VK_SPACE,
            snakeControllerCommandFactory::createNewGameSnakeControllerCommand);

        releasedKeyCodesHandlers.put(KeyEvent.VK_P, snakeControllerCommandFactory::createPauseSnakeControllerCommand);

        pressedKeyCodesHandlers.put(KeyEvent.VK_UP,
            () -> snakeControllerCommandFactory.createDirectionSnakeControllerCommand(Direction.UP));

        releasedKeyCodesHandlers.put(KeyEvent.VK_M, snakeControllerCommandFactory::createMuteSnakeControllerCommand);

        releasedKeyCodesHandlers.put(KeyEvent.VK_PAGE_UP,
            snakeControllerCommandFactory::createIncreaseVolumeSnakeControllerCommand);

        releasedKeyCodesHandlers.put(KeyEvent.VK_PAGE_DOWN,
            snakeControllerCommandFactory::createDecreaseVolumeSnakeControllerCommand);
    }

    @Override
    public void onKeyPressed(final int keyCode) {
        pressedKeysBitSet.set(keyCode);
    }

    @Override
    public void onKeyReleased(final int keyCode) {
        releasedKeysBitSet.set(keyCode);
    }

    @Override
    public List<SnakeControllerCommand> handleInputs() {
        return ListUtils.union(getCommandsFromKeys(pressedKeysBitSet, pressedKeyCodesHandlers),
            getCommandsFromKeys(releasedKeysBitSet, releasedKeyCodesHandlers));
    }

    @Override
    public void reset() {
        pressedKeysBitSet.clear();
        releasedKeysBitSet.clear();
    }

    private List<SnakeControllerCommand> getCommandsFromKeys(final BitSet bitSet,
            final Map<Integer, Supplier<SnakeControllerCommand>> keyCodesHandlers) {

        if (bitSet.isEmpty()) {
            return Collections.emptyList();
        }

        return keyCodesHandlers.keySet().stream().filter(keyCode -> isKeyTriggered(keyCode, bitSet))
                .map(keyCode -> getCommandFromKeyCode(keyCodesHandlers, keyCode)).collect(Collectors.toList());
    }

    private SnakeControllerCommand getCommandFromKeyCode(
            final Map<Integer, Supplier<SnakeControllerCommand>> keyCodesHandlers, final Integer keyCode) {
        return keyCodesHandlers.get(keyCode).get();
    }

    private boolean isKeyTriggered(final int keyCode, final BitSet bitSet) {
        return bitSet.get(keyCode);
    }
}
