package net.lindseybot.shared.worker.impl;

import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.lindseybot.shared.entities.discord.FAttachment;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.worker.legacy.FakeSlashCommand;
import net.lindseybot.shared.worker.services.DiscordAdapter;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MessengerImpl implements Messenger {

    private final DiscordAdapter adapter;

    public MessengerImpl(DiscordAdapter adapter) {
        this.adapter = adapter;
    }

    private Message getContent(FMessage message, Member member) {
        return this.adapter.getMessage(message, member);
    }

    @Override
    public void reply(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message) {
        Message content = getContent(message, event.getMember());
        if (event instanceof FakeSlashCommand fake) {
            this.reply(fake.getMessage(), message);
        } else if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .allowedMentions(this.adapter.allowed(message));
            this.addFiles(hook, message);
            hook.queue(h -> this.selfDestruct(h, message));
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .allowedMentions(this.adapter.allowed(message));
            this.addFiles(hook, message);
            hook.queue(h -> this.selfDestruct(h, message));
        }
    }

    @Override
    public void edit(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message) {
        Message content = getContent(message, event.getMember());
        if (event instanceof FakeSlashCommand fake) {
            this.reply(fake.getMessage(), message);
        } else if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .setEphemeral(message.isEphemeral())
                    .editOriginal(content);
            this.addFiles(hook, message);
            hook.queue(h -> this.selfDestruct(h, message));
        } else {
            this.reply(event, message);
        }
    }

    // --

    @Override
    public void reply(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message) {
        Message content = getContent(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .allowedMentions(this.adapter.allowed(message));
            this.addFiles(hook, message);
            hook.queue(h -> this.selfDestruct(h, message));
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .allowedMentions(this.adapter.allowed(message));
            this.addFiles(hook, message);
            hook.queue(h -> this.selfDestruct(h, message));
        }
    }

    @Override
    public void edit(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message) {
        Message content = getContent(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .editOriginal(content);
            this.addFiles(hook, message);
            hook.queue(h -> this.selfDestruct(h, message));
        } else {
            var hook = event.editMessage(content);
            this.addFiles(hook, message);
            hook.queue(h -> this.selfDestruct(h, message));
        }
    }

    @Override
    public void send(GuildMessageChannel channel, FMessage message) {
        Message content = this.adapter.getMessage(message, channel);
        var hook = channel.sendMessage(content)
                .allowedMentions(this.adapter.allowed(message));
        this.addFiles(hook, message);
        hook.queue(m -> this.selfDestruct(m, message));
    }

    @Override
    public void reply(Message message, FMessage reply) {
        Message content = this.adapter.getMessage(reply, message.getChannel());
        var hook = message.reply(content)
                .mentionRepliedUser(false)
                .allowedMentions(this.adapter.allowed(reply));
        this.addFiles(hook, reply);
        hook.queue(m -> this.selfDestruct(m, reply));
    }

    private void selfDestruct(Message message, FMessage data) {
        if (data.getSelfDestruct() == null) {
            return;
        }
        message.delete()
                .queueAfter(data.getSelfDestruct(), TimeUnit.MILLISECONDS);
    }

    private void selfDestruct(InteractionHook hook, FMessage data) {
        if (data.getSelfDestruct() == null) {
            return;
        }
        hook.deleteOriginal()
                .queueAfter(data.getSelfDestruct(), TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void addFiles(RestAction<?> restAction, FMessage data) {
        if (data.getAttachments().isEmpty()) {
            return;
        }
        if (restAction instanceof MessageAction action) {
            action.retainFiles(Collections.emptyList());
            for (FAttachment attachment : data.getAttachments()) {
                action.addFile(attachment.getStream(), attachment.getName(), attachment.getFlags());
            }
        } else if (restAction instanceof ReplyCallbackAction reply) {
            for (FAttachment attachment : data.getAttachments()) {
                reply.addFile(attachment.getStream(), attachment.getName(), attachment.getFlags());
            }
        } else if (restAction instanceof MessageEditCallbackAction edit) {
            edit.retainFiles(Collections.emptyList());
            for (FAttachment attachment : data.getAttachments()) {
                edit.addFile(attachment.getStream(), attachment.getName(), attachment.getFlags());
            }
        } else if (restAction instanceof WebhookMessageUpdateAction hook) {
            //noinspection unchecked
            hook.retainFiles(Collections.emptyList());
            for (FAttachment attachment : data.getAttachments()) {
                hook.addFile(attachment.getStream(), attachment.getName(), attachment.getFlags());
            }
        } else if (restAction instanceof WebhookMessageAction hook) {
            for (FAttachment attachment : data.getAttachments()) {
                hook.addFile(attachment.getStream(), attachment.getName(), attachment.getFlags());
            }
        }
    }

}
