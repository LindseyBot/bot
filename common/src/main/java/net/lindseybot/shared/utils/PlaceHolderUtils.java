package net.lindseybot.shared.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class PlaceHolderUtils {

    public static String replace(String message, ISnowflake... entries) {
        for (ISnowflake entry : entries) {
            if (entry instanceof Member member) {
                message = PlaceHolderUtils.replace(message, member);
            } else if (entry instanceof User user) {
                message = PlaceHolderUtils.replace(message, user);
            } else if (entry instanceof GuildChannel channel) {
                message = PlaceHolderUtils.replace(message, channel);
            } else if (entry instanceof Guild guild) {
                message = PlaceHolderUtils.replace(message, guild);
            }
        }
        return message;
    }

    private static String replace(String message, Member member) {
        message = message.replace("${User.Name}", member.getEffectiveName());
        message = message.replace("${User.Tag}", member.getUser().getAsTag());
        message = message.replace("${User.Id}", member.getId());
        message = message.replace("${User.Mention}", member.getAsMention());
        message = message.replace("${User.Created}", "<t:" + member.getTimeCreated().toEpochSecond() + ":R>");
        message = message.replace("${User.Joined}", "<t:" + member.getTimeJoined().toEpochSecond() + ":R>");
        return message;
    }

    private static String replace(String message, User user) {
        message = message.replace("${User.Name}", user.getName());
        message = message.replace("${User.Tag}", user.getAsTag());
        message = message.replace("${User.Id}", user.getId());
        message = message.replace("${User.Created}", "<t:" + user.getTimeCreated().toEpochSecond() + ":R>");
        message = message.replace("${User.Mention}", user.getAsMention());
        return message;
    }

    private static String replace(String message, GuildChannel channel) {
        message = message.replace("${Channel.Name}", channel.getName());
        message = message.replace("${Channel.Id}", channel.getId());
        message = message.replace("${Channel.Mention}", channel.getAsMention());
        return message;
    }

    private static String replace(String message, Guild guild) {
        message = message.replace("${Guild.Name}", guild.getName());
        message = message.replace("${Guild.Id}", guild.getId());
        message = message.replace("${Guild.Description}", guild.getDescription() != null ? guild.getDescription() : "");
        message = message.replace("${Guild.Created}", "<t:" + guild.getTimeCreated().toEpochSecond() + ":R>");
        return message;
    }

}
