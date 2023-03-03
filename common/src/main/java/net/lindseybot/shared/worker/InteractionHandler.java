package net.lindseybot.shared.worker;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class InteractionHandler {

    protected final Messenger msg;

    protected InteractionHandler(Messenger msg) {
        this.msg = msg;
    }

    protected @Nullable String getData(ButtonInteractionEvent event) {
        String id = event.getComponentId();
        if (!id.contains(":")) {
            return null;
        }
        return id.substring(id.indexOf(":") + 1);
    }

    protected @Nullable String getData(StringSelectInteractionEvent event) {
        String id = event.getComponentId();
        if (!id.contains(":")) {
            return null;
        }
        return id.substring(id.indexOf(":") + 1);
    }

    protected @Nullable String getSelected(StringSelectInteractionEvent event) {
        List<SelectOption> option = event.getSelectedOptions();
        if (option.isEmpty()) {
            return null;
        }
        return option.get(0).getValue();
    }

    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T> getOption(@NotNull String name,
                                              @NotNull ModalInteractionEvent event,
                                              @NotNull Class<T> tClass) {
        ModalMapping mapping = event.getValue(name);
        if (mapping == null) {
            return Optional.empty();
        }
        if (String.class.equals(tClass)) {
            return Optional.of((T) mapping.getAsString());
        } else if (Boolean.class.equals(tClass)) {
            return Optional.of((T) OptionUtils.parseBoolean(mapping.getAsString()));
        } else if (Integer.class.equals(tClass)) {
            try {
                return Optional.of((T) Integer.valueOf(mapping.getAsString()));
            } catch (IllegalArgumentException ex) {
                return Optional.empty();
            }
        } else if (Long.class.equals(tClass)) {
            try {
                return Optional.of((T) Long.valueOf(mapping.getAsString()));
            } catch (IllegalArgumentException ex) {
                return Optional.empty();
            }
        }
        throw new IllegalStateException("Unexpected value: " + tClass);
    }

    @SuppressWarnings("unchecked")
    protected @Nullable <T> T getOption(@NotNull String name,
                                        @NotNull SlashCommandInteractionEvent event,
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
            return (T) mapping.getAsChannel();
        } else if (VoiceChannel.class.equals(tClass)) {
            if (mapping.getChannelType() != ChannelType.VOICE) {
                return null;
            }
            return (T) mapping.getAsChannel();
        } else if (Role.class.equals(tClass)) {
            return (T) mapping.getAsRole();
        } else if (Message.Attachment.class.equals(tClass)) {
            return (T) mapping.getAsAttachment();
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
