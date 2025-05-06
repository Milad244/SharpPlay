package com.milad.core;

public enum PlayMode {
    ONE_LOOP, LOOP, SHUFFLE;

    public PlayMode toggle() {
        PlayMode[] modes = values();
        return modes[(this.ordinal() + 1) % modes.length];
    }
}
