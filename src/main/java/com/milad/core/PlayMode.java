package com.milad.core;

/**
 * An enum that represents various play modes for MusicPlayer to use.
 */
public enum PlayMode {
    ONE_LOOP, LOOP, SHUFFLE;

    /**
     * Toggles between the different play modes.
     * @return the next play mode, as a PlayMode enum
     */
    public PlayMode toggle() {
        PlayMode[] modes = values();
        return modes[(this.ordinal() + 1) % modes.length];
    }
}
