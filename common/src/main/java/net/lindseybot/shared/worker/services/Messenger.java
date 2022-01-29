package net.lindseybot.shared.worker.services;

import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import org.jetbrains.annotations.NotNull;

public interface Messenger {

    /**
     * Replies to a slash command with an ephemeral message.
     *
     * @param event SlashCommandInteractionEvent.
     * @param label Message content.
     */
    default void error(@NotNull SlashCommandInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a button click with an ephemeral message.
     *
     * @param event ButtonInteractionEvent.
     * @param label Message content.
     */
    default void error(@NotNull ButtonInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a selection menu click with an ephemeral message.
     *
     * @param event SelectionMenuEvent.
     * @param label Message content.
     */
    default void error(@NotNull SelectMenuInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a slash command.
     *
     * @param event   SlashCommandInteractionEvent.
     * @param message Message response.
     */
    void reply(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message);

    /**
     * Replies to a slash command.
     *
     * @param event SlashCommandInteractionEvent.
     * @param label Message content.
     */
    default void reply(@NotNull SlashCommandInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label));
    }

    void edit(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message);

    default void edit(@NotNull SlashCommandInteractionEvent event, @NotNull Label label) {
        this.edit(event, FMessage.of(label));
    }

    void reply(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message);

    default void reply(@NotNull GenericComponentInteractionCreateEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label));
    }

    void edit(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message);

    default void edit(@NotNull GenericComponentInteractionCreateEvent event, @NotNull Label label) {
        this.edit(event, FMessage.of(label));
    }

    /**
     * Sends a message to a text-channel.
     *
     * @param channel Channel.
     * @param message Message.
     */
    void send(GuildMessageChannel channel, FMessage message);

    /**
     * Sends a message to a text-channel.
     *
     * @param channel Channel.
     * @param label   Message content.
     */
    default void send(GuildMessageChannel channel, Label label) {
        this.send(channel, FMessage.of(label));
    }

    /**
     * Replies to a message on a text-channel.
     *
     * @param message Original message.
     * @param reply   Reply message.
     */
    void reply(Message message, FMessage reply);

    /**
     * Replies to a message in a message channel.
     *
     * @param message Original message.
     * @param label   Message content.
     */
    default void reply(Message message, Label label) {
        this.reply(message, FMessage.of(label));
    }

    /**
     * Replies to a message in a message channel.
     *
     * @param event Original message.
     * @param reply Message content.
     */
    default void reply(MessageReceivedEvent event, FMessage reply) {
        this.reply(event.getMessage(), reply);
    }

    /**
     * Replies to a message in a message channel.
     *
     * @param event Original message.
     * @param label Message content.
     */
    default void reply(MessageReceivedEvent event, Label label) {
        this.reply(event.getMessage(), label);
    }

}
