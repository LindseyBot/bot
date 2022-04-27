package net.lindseybot.moderation.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class SoftBan extends InteractionHandler {

    public SoftBan(Messenger messenger) {
        super(messenger);
    }

    @SlashCommand(value = "softban", guildOnly = true)
    @SuppressWarnings("CodeBlock2Expr")
    public void onCommand(SlashCommandInteractionEvent event) {
        Long userId = this.getOption("user", event, Long.class);
        String reason = this.getOption("reason", event, String.class);
        Member member = event.getMember();
        if (member == null) {
            return;
        } else if (!member.hasPermission(Permission.BAN_MEMBERS)) {
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        Guild guild = event.getGuild();
        if (guild == null) {
            return;
        }
        event.deferReply(false).queue((a) -> {
            try {
                guild.ban(UserSnowflake.fromId(userId), 7, reason)
                        .delay(1, TimeUnit.SECONDS)
                        .and(guild.unban(UserSnowflake.fromId(userId)))
                        .queue((aVoid) -> {
                            this.msg.reply(event, Label.of("commands.softban.success"));
                        }, throwable -> {
                            this.msg.reply(event, Label.of("error.discord", throwable.getMessage()));
                        });
            } catch (InsufficientPermissionException ex) {
                this.msg.reply(event, FMessage.of(Label.of("permissions.bot", ex.getPermission().getName()), true));
            } catch (HierarchyException ex) {
                this.msg.reply(event, FMessage.of(Label.of("permissions.hierarchy"), true));
            }
        }, noop());
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
