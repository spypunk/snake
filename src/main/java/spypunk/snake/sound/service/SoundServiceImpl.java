/*
 * Copyright © 2016 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.sound.service;

import javax.inject.Inject;
import javax.inject.Singleton;

import spypunk.snake.sound.Sound;
import spypunk.snake.sound.SoundClip;
import spypunk.snake.sound.cache.SoundClipCache;

@Singleton
public class SoundServiceImpl implements SoundService {

    private final SoundClipCache soundClipCache;

    private SoundClip currentMusicSoundClip;

    @Inject
    public SoundServiceImpl(final SoundClipCache soundClipCache) {
        this.soundClipCache = soundClipCache;
    }

    @Override
    public void playMusic(final Sound sound) {
        stopMusic();

        currentMusicSoundClip = soundClipCache.getSoundClip(sound);

        currentMusicSoundClip.play();
    }

    @Override
    public void pauseMusic() {
        if (currentMusicSoundClip != null) {
            currentMusicSoundClip.pause();
        }
    }

    @Override
    public void resumeMusic() {
        if (currentMusicSoundClip != null) {
            currentMusicSoundClip.play();
        }
    }

    @Override
    public void stopMusic() {
        if (currentMusicSoundClip != null) {
            currentMusicSoundClip.stop();
            currentMusicSoundClip = null;
        }
    }

    @Override
    public void playSound(final Sound sound) {
        final SoundClip clip = soundClipCache.getSoundClip(sound);

        clip.stop();
        clip.play();
    }

    @Override
    public void setMuted(final boolean muted) {
        soundClipCache.getAllSoundClips().forEach(soundClip -> soundClip.setMuted(muted));
    }

    @Override
    public void increaseVolume() {
        soundClipCache.getAllSoundClips().forEach(SoundClip::increaseVolume);
    }

    @Override
    public void decreaseVolume() {
        soundClipCache.getAllSoundClips().forEach(SoundClip::decreaseVolume);
    }
}
