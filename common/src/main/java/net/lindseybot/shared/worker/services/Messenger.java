package net.lindseybot.shared.worker.services;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
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
    default void error(@NotNull StringSelectInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a user context command with an ephemeral message.
     *
     * @param event UserContextCommand.
     * @param label Message content.
     */
    default void error(@NotNull UserContextInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a message context command with an ephemeral message.
     *
     * @param event MessageContextCommand.
     * @param label Message content.
     */
    default void error(@NotNull MessageContextInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a modal interaction with an ephemeral message.
     *
     * @param event ModalInteraction.
     * @param label Message content.
     */
    default void error(@NotNull ModalInteractionEvent event, @NotNull Label label) {
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
     * Replies to a component interaction.
     *
     * @param event   GenericComponentInteractionCreateEvent.
     * @param message Message response.
     */
    void reply(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message);

    /**
     * Replies to a message context command.
     *
     * @param event   MessageContextInteractionEvent.
     * @param message Message response.
     */
    void reply(@NotNull MessageContextInteractionEvent event, @NotNull FMessage message);

    /**
     * Replies to a user context command.
     *
     * @param event   UserContextInteractionEvent.
     * @param message Message response.
     */
    void reply(@NotNull UserContextInteractionEvent event, @NotNull FMessage message);

    /**
     * Replies to a modal.
     *
     * @param event   ModalInteraction.
     * @param message Message response.
     */
    void reply(@NotNull ModalInteractionEvent event, @NotNull FMessage message);

    /**
     * Replies to a slash command.
     *
     * @param event SlashCommandInteractionEvent.
     * @param label Message content.
     */
    default void reply(@NotNull SlashCommandInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label));
    }

    /**
     * Replies to a component interaction.
     *
     * @param event GenericComponentInteractionCreateEvent.
     * @param label Message content.
     */
    default void reply(@NotNull GenericComponentInteractionCreateEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label));
    }

    /**
     * Replies to a message context command.
     *
     * @param event MessageContextInteractionEvent.
     * @param label Message content.
     */
    default void reply(@NotNull MessageContextInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label));
    }

    /**
     * Replies to a user context command.
     *
     * @param event UserContextInteractionEvent.
     * @param label Message content.
     */
    default void reply(@NotNull UserContextInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label));
    }

    /**
     * Replies to a modal.
     *
     * @param event ModalInteraction.
     * @param label Message content.
     */
    default void reply(@NotNull ModalInteractionEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label));
    }

    void edit(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message);

    void edit(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message);

    void edit(@NotNull MessageContextInteractionEvent event, @NotNull FMessage message);

    void edit(@NotNull UserContextInteractionEvent event, @NotNull FMessage message);

    default void edit(@NotNull SlashCommandInteractionEvent event, @NotNull Label label) {
        this.edit(event, FMessage.of(label));
    }

    default void edit(@NotNull GenericComponentInteractionCreateEvent event, @NotNull Label label) {
        this.edit(event, FMessage.of(label));
    }

    default void edit(@NotNull MessageContextInteractionEvent event, @NotNull Label label) {
        this.edit(event, FMessage.of(label));
    }

    default void edit(@NotNull UserContextInteractionEvent event, @NotNull Label label) {
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
