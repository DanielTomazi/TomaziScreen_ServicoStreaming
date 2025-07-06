package com.tomazi.streaming.domain.entities;

public enum ContentStatus {
    UPLOADED("Uploaded"),
    PROCESSING("Processing"),
    PROCESSED("Processed"),
    PUBLISHED("Published"),
    UNPUBLISHED("Unpublished"),
    DELETED("Deleted"),
    FAILED("Failed");

    private final String displayName;

    ContentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
