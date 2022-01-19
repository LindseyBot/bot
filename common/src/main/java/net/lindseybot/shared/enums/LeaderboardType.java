package net.lindseybot.shared.enums;

import lombok.Getter;

public enum LeaderboardType {

    COOKIES("Cookie"), SLOT_WINS("Slot wins"), DAILY_STREAK("Daily Streak");

    @Getter
    private final String prettyName;

    LeaderboardType(String prettyName) {
        this.prettyName = prettyName;
    }

    public static LeaderboardType fromString(String arg) {
        for (LeaderboardType type : LeaderboardType.values()) {
            if (type.name().equalsIgnoreCase(arg)) {
                return type;
            }
            if (type.prettyName.equalsIgnoreCase(arg)) {
                return type;
            }
            if (type.name().toLowerCase().replace("_", " ").contains(arg.toLowerCase())) {
                return type;
            }
            if (type.prettyName.toLowerCase().contains(arg.toLowerCase())) {
                return type;
            }
        }
        return null;
    }

}
