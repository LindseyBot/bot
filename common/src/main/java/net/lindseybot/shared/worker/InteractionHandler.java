package net.lindseybot.shared.worker;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InteractionHandler {

    protected final Messenger msg;

    protected InteractionHandler(Messenger msg) {
        this.msg = msg;
    }

    protected @Nullable String getData(ButtonClickEvent event) {
        String id = event.getComponentId();
        if (!id.contains(":")) {
            return null;
        }
        return id.substring(id.indexOf(":") + 1);
    }

    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getOption(@NotNull String name,
                                        @NotNull SlashCommandEvent event,
                                        @NotNull Class<T> tClass) {
        OptionMapping mapping = event.getOption(name);
        if (mapping == null) {
            return null;
        }
        if (String.class.equals(tClass)) {
            return (T) mapping.getAsString();
        } else if (Long.class.equals(tClass)) {
            return (T) Long.valueOf(mapping.getAsLong());
        } else if (Double.class.equals(tClass)) {
            return (T) Double.valueOf(mapping.getAsDouble());
        } else if (Boolean.class.equals(tClass)) {
            return (T) Boolean.valueOf(mapping.getAsBoolean());
        } else if (User.class.equals(tClass)) {
            return (T) mapping.getAsUser();
        } else if (Member.class.equals(tClass)) {
            return (T) mapping.getAsMember();
        } else if (TextChannel.class.equals(tClass)) {
            if (mapping.getChannelType() != ChannelType.TEXT) {
                return null;
            }
            return (T) mapping.getAsGuildChannel();
        } else if (VoiceChannel.class.equals(tClass)) {
            if (mapping.getChannelType() != ChannelType.VOICE) {
                return null;
            }
            return (T) mapping.getAsGuildChannel();
        } else if (Role.class.equals(tClass)) {
            return (T) mapping.getAsRole();
        }
        throw new IllegalStateException("Unexpected value: " + tClass);
    }

    protected @NotNull String getAsMention(@Nullable GuildChannel channel) {
        if (channel == null) {
            return "<Unknown>";
        }
        return channel.getAsMention();
    }

    protected @NotNull String getAsTag(@Nullable Member member) {
        if (member == null) {
            return "Unknown?";
        }
        return member.getUser().getAsTag();
    }

    protected @NotNull String getAsMention(@Nullable Member member) {
        if (member == null) {
            return "<Unknown>";
        }
        return member.getAsMention();
    }

}