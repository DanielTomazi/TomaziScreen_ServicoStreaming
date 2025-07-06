package com.tomazi.streaming.domain.entities;

public enum ContentType {
    VIDEO("Video"),
    LIVE_STREAM("Live Stream"),
    PODCAST("Podcast"),
    AUDIO("Audio");

    private final String displayName;

    ContentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
