package net.lindseybot.shared.utils;

public class DiscordCDN {

    private static final String BASE_CDN_URL = "https://cdn.discordapp.com";

    public static String avatarUrl(String hash, Long userId) {
        return BASE_CDN_URL + "/avatars/" + userId + "/" + hash + (hash.startsWith("a_") ? ".gif" : ".webp");
    }

    public static String defaultAvatar(String discriminator) {
        return BASE_CDN_URL + "/embed/avatars/" + (Integer.parseInt(discriminator) % 5) + ".png";
    }

    public static String guildUrl(String hash, Long guildId) {
        return BASE_CDN_URL + "/icons/" + guildId + "/" + hash + (hash.startsWith("a_") ? ".gif" : ".webp");
    }

}
