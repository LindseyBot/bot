package net.lindseybot.moderation.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
public class VoiceSplit extends InteractionHandler {

    public VoiceSplit(Messenger messenger) {
        super(messenger);
    }

    @SlashCommand("voice.split")
    @SuppressWarnings("CodeBlock2Expr")
    public void onCommand(SlashCommandInteractionEvent event) {
        OptionMapping from = event.getOption("from");
        if (from == null) {
            return;
        } else if (from.getChannelType() != ChannelType.VOICE) {
            this.msg.error(event, Label.of("validation.voice", from.getAsChannel().getAsMention()));
            return;
        }
        VoiceChannel fromChannel = from.getAsChannel().asVoiceChannel();
        OptionMapping target = event.getOption("to");
        if (target == null) {
            return;
        } else if (target.getChannelType() != ChannelType.VOICE) {
            this.msg.error(event, Label.of("validation.voice", target.getAsChannel().getAsMention()));
            return;
        }
        int members = fromChannel.getMembers().size();
        if (members == 0) {
            this.msg.error(event, Label.of("commands.voice.split.empty"));
            return;
        } else if (members == 1) {
            this.msg.error(event, Label.of("commands.voice.split.half"));
            return;
        }
        VoiceChannel toChannel = target.getAsChannel().asVoiceChannel();
        Member member = event.getMember();
        if (member == null) {
            return;
        }
        Guild guild = event.getGuild();
        if (guild == null) {
            return;
        } else if (!event.getMember().hasPermission(Permission.VOICE_MOVE_OTHERS)) {
            log.warn("Permission check prevented use of voice split on {}", guild.getId());
            this.msg.error(event, Label.of("permissions.user"));
            return;
        }
        event.deferReply(false).queue((a) -> {
            try {
                int toMove = members / 2;
                List<RestAction<Void>> actions = fromChannel.getMembers().stream()
                        .limit(toMove)
                        .map(m -> guild.moveVoiceMember(m, toChannel))
                        .collect(Collectors.toList());
                RestAction.allOf(actions).queue((aVoid) -> {
                    this.msg.reply(event, Label.of("commands.voice.split.success", toMove));
                }, throwable -> {
                    this.msg.error(event, Label.of("error.discord", throwable.getMessage()));
                });
            } catch (InsufficientPermissionException ex) {
                this.msg.error(event, Label.of("permissions.bot", ex.getPermission().getName()));
            } catch (HierarchyException ex) {
                this.msg.error(event, Label.of("permissions.hierarchy"));
            }
        }, this.noop());
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
