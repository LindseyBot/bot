package net.lindseybot.moderation.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class HackBan extends InteractionHandler {

    public HackBan(Messenger messenger) {
        super(messenger);
    }

    @SlashCommand(value = "hackban", guildOnly = true)
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
                guild.ban(String.valueOf(userId), 7, reason).queue((aVoid) -> {
                    this.msg.reply(event, Label.of("commands.hackban.success"));
                }, throwable -> {
                    this.msg.reply(event, Label.of("error.discord", throwable.getMessage()));
                });
            } catch (InsufficientPermissionException ex) {
                this.msg.error(event, Label.of("permissions.bot", ex.getPermission().getName()));
            } catch (HierarchyException ex) {
                this.msg.error(event, Label.of("permissions.hierarchy"));
            }
        }, noop());
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
