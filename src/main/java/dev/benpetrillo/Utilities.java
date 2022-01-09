package dev.benpetrillo;

import java.util.List;

public final class Utilities {

    /**
     * Check if a user is an application developer.
     * @param id The user ID to check.
     * @return boolean
     */

    public static boolean isApplicationDeveloper(String id) {
        return List.of(Config.get("DEVELOPERS").split(",")).contains(id);
    }
}
