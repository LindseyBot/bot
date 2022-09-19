package net.lindseybot.moderation.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.MessageCommand;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class PruneCommand extends InteractionHandler {

    public PruneCommand(Messenger messenger) {
        super(messenger);
    }

    @SlashCommand(value = "prune", guildOnly = true)
    public void onCommand(SlashCommandInteractionEvent event) {
        Long count = this.getOption("count", event, Long.class);
        if (count == null || count < 1) {
            this.msg.error(event, Label.of("commands.prune.invalid"));
            return;
        } else if (count > 500) {
            this.msg.error(event, Label.of("commands.prune.max"));
            return;
        }
        Long userId = this.getOption("user", event, Long.class);
        Member member = event.getMember();
        if (member == null) {
            return;
        } else if (!member.hasPermission(event.getGuildChannel(), Permission.MESSAGE_MANAGE)) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        event.deferReply(false).queue((hook) -> {
            try {
                List<Message> messages = this.findUntil(event.getMessageChannel(), count, (m) -> {
                    return userId == null || m.getAuthor().getIdLong() == userId;
                }, (m) -> {
                    return false;
                });
                if (messages.isEmpty()) {
                    this.msg.error(event, Label.of("commands.prune.empty"));
                    return;
                }
                this.deleteMessages(event.getMessageChannel(), messages);
                FMessage msg = FMessage.of(Label.of("commands.prune.success", messages.size()));
                msg.setSelfDestruct(5000L);
                this.msg.reply(event, msg);
            } catch (PermissionException ex) {
                this.msg.error(event, Label.of("permissions.bot", ex.getPermission().getName()));
            }
        }, this.noop());
    }

    @MessageCommand("Delete until here")
    public void onDeleteUntilHere(MessageContextInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return;
        } else if (!member.hasPermission(event.getGuildChannel(), Permission.MESSAGE_MANAGE)) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        MessageChannel channel = event.getChannel();
        if (channel == null) {
            return;
        }
        Message target = event.getTarget();
        if (target.getTimeCreated()
                .isBefore(OffsetDateTime.now().minusDays(14))) {
            this.msg.error(event, Label.raw("This message is too old."));
            return;
        }
        event.deferReply(false).queue((a) -> {
            try {
                List<Message> messages = this.findUntil(channel, 1000L, (m) -> {
                    return true;
                }, (m) -> {
                    return m.getId().equals(target.getId());
                });
                if (messages.isEmpty()) {
                    this.msg.error(event, Label.of("commands.prune.empty"));
                    return;
                }
                this.deleteMessages(event.getMessageChannel(), messages);
                FMessage msg = FMessage.of(Label.of("commands.prune.success", messages.size()));
                msg.setSelfDestruct(5000L);
                this.msg.reply(event, msg);
            } catch (PermissionException ex) {
                this.msg.error(event, Label.of("permissions.bot", ex.getPermission().getName()));
            }
        }, this.noop());
    }

    private void deleteMessages(MessageChannel channel, List<Message> messages)
            throws PermissionException {
        CompletableFuture.allOf(channel.purgeMessages(messages)
                .toArray(new CompletableFuture[0])).join();
    }

    private List<Message> findUntil(MessageChannel channel, Long limit,
                                    Function<Message, Boolean> check,
                                    Function<Message, Boolean> stop) {
        AtomicInteger taken = new AtomicInteger();
        List<Message> messages = new ArrayList<>();
        channel.getIterableHistory().cache(false).forEachAsync(message -> {
            // Add conditions
            if (taken.incrementAndGet() == 1
                    && message.getAuthor().isBot()) {
                return true;
            } else if (message.getTimeCreated()
                    .isBefore(OffsetDateTime.now().minusDays(14))) {
                return false;
            } else if (check.apply(message)) {
                messages.add(message);
            }
            // Stop conditions
            if (stop.apply(message)) {
                return false;
            }
            return taken.get() <= limit;
        }).join();
        return messages;
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
