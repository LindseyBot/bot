package net.lindseybot.shared.worker.impl;

import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.worker.services.DiscordAdapter;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class DefaultMessenger implements Messenger {

    private final DiscordAdapter adapter;

    public DefaultMessenger(DiscordAdapter adapter) {
        this.adapter = adapter;
    }

    private Message getContent(FMessage message, Member member) {
        return this.adapter.getMessage(message, member);
    }

    @Override
    public void reply(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message) {
        Message content = getContent(message, event.getMember());
        if (event.isAcknowledged()) {
            if (message.isEphemeral()) {
                event.getHook()
                        .sendMessage(content)
                        .setEphemeral(true)
                        .allowedMentions(this.adapter.allowed(message))
                        .queue();
            } else if (message.getSelfDestruct() != null) {
                event.getHook()
                        .sendMessage(content)
                        .allowedMentions(this.adapter.allowed(message))
                        .queue(m -> m.delete().queueAfter(message.getSelfDestruct(), TimeUnit.MILLISECONDS));
            } else {
                event.getHook()
                        .sendMessage(content)
                        .allowedMentions(this.adapter.allowed(message))
                        .queue();
            }
        } else if (message.isEphemeral()) {
            event.reply(content)
                    .setEphemeral(true)
                    .allowedMentions(this.adapter.allowed(message))
                    .queue();
        } else if (message.getSelfDestruct() != null) {
            event.reply(content)
                    .allowedMentions(this.adapter.allowed(message))
                    .queue(h -> {
                        if (h != null) {
                            h.deleteOriginal().queueAfter(message.getSelfDestruct(), TimeUnit.MILLISECONDS);
                        }
                    });
        } else {
            event.reply(content)
                    .allowedMentions(this.adapter.allowed(message))
                    .queue();
        }
    }

    @Override
    public void edit(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message) {
        Message content = getContent(message, event.getMember());
        if (event.isAcknowledged()) {
            event.getHook()
                    .editOriginal(content)
                    .queue();
        } else {
            this.reply(event, message);
        }
    }

    @Override
    public void reply(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message) {
        Message content = getContent(message, event.getMember());
        if (event.isAcknowledged()) {
            event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .allowedMentions(this.adapter.allowed(message))
                    .queue();
        } else {
            event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .allowedMentions(this.adapter.allowed(message))
                    .queue();
        }
    }

    @Override
    public void edit(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message) {
        Message content = getContent(message, event.getMember());
        if (event.isAcknowledged()) {
            event.getHook()
                    .editOriginal(content)
                    .queue();
        } else {
            event.editMessage(content)
                    .queue();
        }
    }

    @Override
    public void send(GuildMessageChannel channel, FMessage message) {
        Message content = this.adapter.getMessage(message, channel);
        if (message.getSelfDestruct() != null) {
            channel.sendMessage(content)
                    .allowedMentions(this.adapter.allowed(message))
                    .queue(m -> m.delete().queueAfter(message.getSelfDestruct(), TimeUnit.MILLISECONDS));
        } else {
            channel.sendMessage(content)
                    .allowedMentions(this.adapter.allowed(message))
                    .queue();
        }
    }

    @Override
    public void reply(Message message, FMessage reply) {
        Message content = this.adapter.getMessage(reply, message.getChannel());
        if (reply.getSelfDestruct() != null) {
            message.reply(content)
                    .allowedMentions(this.adapter.allowed(reply))
                    .queue(m -> m.delete().queueAfter(reply.getSelfDestruct(), TimeUnit.MILLISECONDS));
        } else {
            message.reply(content)
                    .allowedMentions(this.adapter.allowed(reply))
                    .queue();
        }
    }

}
