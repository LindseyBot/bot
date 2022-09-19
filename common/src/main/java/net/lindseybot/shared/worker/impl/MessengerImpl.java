package net.lindseybot.shared.worker.impl;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.worker.legacy.FakeSlashCommand;
import net.lindseybot.shared.worker.services.DiscordAdapter;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MessengerImpl implements Messenger {

    private final DiscordAdapter adapter;

    public MessengerImpl(DiscordAdapter adapter) {
        this.adapter = adapter;
    }

    private MessageCreateData toNew(FMessage message, ISnowflake member) {
        return this.adapter.toNew(message, member);
    }

    private MessageEditData toEdit(FMessage message, ISnowflake member) {
        return this.adapter.toEdit(message, member);
    }

    @Override
    public void reply(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message) {
        MessageCreateData content = this.toNew(message, event.getMember());
        if (event instanceof FakeSlashCommand fake) {
            this.reply(fake.getMessage(), message);
        } else if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        }
    }

    @Override
    public void reply(@NotNull MessageContextInteractionEvent event, @NotNull FMessage message) {
        MessageCreateData content = this.toNew(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        }
    }

    @Override
    public void reply(@NotNull UserContextInteractionEvent event, @NotNull FMessage message) {
        MessageCreateData content = this.toNew(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        }
    }

    @Override
    public void reply(@NotNull ModalInteractionEvent event, @NotNull FMessage message) {
        MessageCreateData content = this.toNew(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        }
    }

    @Override
    public void edit(@NotNull SlashCommandInteractionEvent event, @NotNull FMessage message) {
        MessageEditData content = this.toEdit(message, event.getMember());
        if (event instanceof FakeSlashCommand fake) {
            this.reply(fake.getMessage(), message);
        } else if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .setEphemeral(message.isEphemeral())
                    .editOriginal(content);
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            this.reply(event, message);
        }
    }

    // --

    @Override
    public void reply(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message) {
        MessageCreateData content = this.toNew(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        }
    }

    @Override
    public void edit(@NotNull GenericComponentInteractionCreateEvent event, @NotNull FMessage message) {
        MessageEditData content = this.toEdit(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .editOriginal(content);
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            var hook = event.editMessage(content);
            hook.queue(h -> this.selfDestruct(h, message), noop());
        }
    }

    @Override
    public void edit(@NotNull MessageContextInteractionEvent event, @NotNull FMessage message) {
        MessageCreateData content = this.toNew(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        }
    }

    @Override
    public void edit(@NotNull UserContextInteractionEvent event, @NotNull FMessage message) {
        MessageCreateData content = this.toNew(message, event.getMember());
        if (event.isAcknowledged()) {
            var hook = event.getHook()
                    .sendMessage(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        } else {
            var hook = event.reply(content)
                    .setEphemeral(message.isEphemeral())
                    .setAllowedMentions(this.adapter.allowed(message));
            hook.queue(h -> this.selfDestruct(h, message), noop());
        }
    }

    @Override
    public void send(GuildMessageChannel channel, FMessage message) {
        if (!channel.canTalk()) {
            return;
        }
        MessageCreateData content = this.toNew(message, channel);
        var hook = channel.sendMessage(content)
                .setAllowedMentions(this.adapter.allowed(message));
        hook.queue(m -> this.selfDestruct(m, message), noop());
    }

    @Override
    public void reply(Message message, FMessage reply) {
        if (!message.getChannel().canTalk()) {
            return;
        }
        MessageCreateData content = this.toNew(reply, message.getChannel());
        var hook = message.reply(content)
                .mentionRepliedUser(false)
                .setAllowedMentions(this.adapter.allowed(reply));
        hook.queue(m -> this.selfDestruct(m, reply), noop());
    }

    private void selfDestruct(Message message, FMessage data) {
        if (data.getSelfDestruct() == null) {
            return;
        }
        message.delete()
                .queueAfter(data.getSelfDestruct(), TimeUnit.MILLISECONDS, noop(), noop());
    }

    private void selfDestruct(InteractionHook hook, FMessage data) {
        if (data.getSelfDestruct() == null) {
            return;
        }
        hook.deleteOriginal()
                .queueAfter(data.getSelfDestruct(), TimeUnit.MILLISECONDS, noop(), noop());
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
