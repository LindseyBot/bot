package net.lindseybot.shared.worker.services;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import org.jetbrains.annotations.NotNull;

public interface Messenger {

    /**
     * Replies to a slash command with an ephemeral message.
     *
     * @param event SlashCommandEvent.
     * @param label Message content.
     */
    default void error(@NotNull SlashCommandEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a button click with an ephemeral message.
     *
     * @param event ButtonClickEvent.
     * @param label Message content.
     */
    default void error(@NotNull ButtonClickEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a selection menu click with an ephemeral message.
     *
     * @param event SelectionMenuEvent.
     * @param label Message content.
     */
    default void error(@NotNull SelectionMenuEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label, true));
    }

    /**
     * Replies to a slash command.
     *
     * @param event   SlashCommandEvent.
     * @param message Message response.
     */
    void reply(@NotNull SlashCommandEvent event, @NotNull FMessage message);

    /**
     * Replies to a slash command.
     *
     * @param event SlashCommandEvent.
     * @param label Message content.
     */
    default void reply(@NotNull SlashCommandEvent event, @NotNull Label label) {
        this.reply(event, FMessage.of(label));
    }

    void edit(@NotNull SlashCommandEvent event, @NotNull FMessage message);

    default void edit(@NotNull SlashCommandEvent event, @NotNull Label label) {
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
    void send(TextChannel channel, FMessage message);

    /**
     * Sends a message to a text-channel.
     *
     * @param channel Channel.
     * @param label   Message content.
     */
    default void send(TextChannel channel, Label label) {
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
    default void reply(GuildMessageReceivedEvent event, FMessage reply) {
        this.reply(event.getMessage(), reply);
    }

    /**
     * Replies to a message in a message channel.
     *
     * @param event Original message.
     * @param label Message content.
     */
    default void reply(GuildMessageReceivedEvent event, Label label) {
        this.reply(event.getMessage(), label);
    }

}
