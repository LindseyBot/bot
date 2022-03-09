package net.lindseybot.moderation.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
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
        TextChannel channel = event.getTextChannel();
        Member member = event.getMember();
        if (member == null) {
            return;
        } else if (!member.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        Guild guild = event.getGuild();
        if (guild == null) {
            return;
        }
        event.deferReply(false).queue((a) -> {
            List<Message> messages = new ArrayList<>();
            AtomicInteger taken = new AtomicInteger();
            try {
                channel.getIterableHistory().cache(false).forEachAsync(m -> {
                    if (taken.incrementAndGet() == 1) {
                        return true;
                    } else if (userId == null || m.getAuthor().getIdLong() == userId) {
                        messages.add(m);
                    }
                    if (messages.size() >= count) {
                        return false;
                    }
                    return taken.get() <= 500;
                }).thenRun(() -> deleteMessages(event, messages));
            } catch (InsufficientPermissionException ex) {
                this.msg.error(event, Label.of("permissions.bot", ex.getPermission().getName()));
            }
        }, this.noop());
    }

    @MessageCommand("Delete until here")
    public void onDeleteUntilHere(MessageContextInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return;
        } else if (!member.hasPermission(Permission.MESSAGE_MANAGE)) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        MessageChannel channel = event.getChannel();
        if (channel == null) {
            return;
        }
        Message target = event.getTarget();
        if (target.getTimeCreated()
                .isBefore(OffsetDateTime.now().minusDays(13))) {
            this.msg.error(event, Label.raw("This message is too old."));
            return;
        }
        event.deferReply(false).queue((a) -> {
            List<Message> messages = new ArrayList<>();
            AtomicInteger taken = new AtomicInteger();
            try {
                channel.getIterableHistory().cache(false).forEachAsync(m -> {
                    if (m.getId().equals(target.getId())) {
                        messages.add(m);
                        return false;
                    } else if (taken.incrementAndGet() == 1) {
                        return true;
                    }
                    messages.add(m);
                    return taken.get() <= 1000;
                }).thenRun(() -> this.deleteMessages(event.getMessageChannel(), messages));
            } catch (InsufficientPermissionException ex) {
                this.msg.error(event, Label.of("permissions.bot", ex.getPermission().getName()));
            }
        }, this.noop());
    }

    private void deleteMessages(MessageChannel channel, List<Message> messages) {
        try {
            CompletableFuture.allOf(channel.purgeMessages(messages)
                    .toArray(new CompletableFuture[0])).join();
            // Success
        } catch (InsufficientPermissionException ex) {
            // In guild but no permission
        } catch (IllegalArgumentException ex) {
            // In dms?
        }
    }

    private void deleteMessages(SlashCommandInteractionEvent event, List<Message> messages) {
        try {
            int size = messages.size();
            if (size == 0) {
                this.msg.error(event, Label.of("commands.prune.empty"));
                return;
            } else if (size == 1) {
                messages.get(0).delete().queue((aVoid) -> {
                    FMessage msg = FMessage.of(Label.of("commands.prune.success", 1));
                    msg.setSelfDestruct(5000L);
                    this.msg.reply(event, msg);
                }, throwable -> {
                    this.msg.error(event, Label.of("error.discord", throwable.getMessage()));
                });
                return;
            }
            event.getTextChannel().deleteMessages(messages).queue((aVoid) -> {
                FMessage msg = FMessage.of(Label.of("commands.prune.success", Math.min(size, 100)));
                msg.setSelfDestruct(5000L);
                this.msg.reply(event, msg);
            }, throwable -> {
                this.msg.error(event, Label.of("error.discord", throwable.getMessage()));
            });
        } catch (InsufficientPermissionException ex) {
            this.msg.error(event, Label.of("permissions.bot", ex.getPermission().getName()));
        }
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
