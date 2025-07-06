package com.tomazi.streaming.domain.entities;

public enum UserRole {
    VIEWER("Viewer"),
    CREATOR("Content Creator"),
    MODERATOR("Moderator"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
