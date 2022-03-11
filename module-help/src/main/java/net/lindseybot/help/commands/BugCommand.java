package net.lindseybot.help.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.lindseybot.shared.entities.discord.FEmbed;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BugCommand extends InteractionHandler {

    private final ShardManager api;

    public BugCommand(Messenger msg, ShardManager api) {
        super(msg);
        this.api = api;
    }

    @SlashCommand(value = "lindsey.bug", ephemeral = true)
    public void onCommand(SlashCommandInteractionEvent event) {
        String message = this.getOption("description", event, String.class);
        if (message == null || message.isBlank()) {
            this.msg.error(event, Label.raw("Invalid message"));
            return;
        }
        Message.Attachment attachment = this.getOption("image", event, Message.Attachment.class);
        if (attachment != null) {
            this.sendBug(event.getUser(), message, attachment.getUrl());
        } else {
            this.sendBug(event.getUser(), message, null);
        }
        log.info("Bug reported: {}", message);
        FMessage msg = new MessageBuilder()
                .ephemeral()
                .content(Label.raw("Bug reported, thanks!"))
                .build();
        this.msg.reply(event, msg);
    }

    private void sendBug(User user, String message, String attachment) {
        Guild guild = this.api.getGuildById("141555945586163712");
        if (guild == null) {
            log.warn("Unable to find bug report guild!");
            return;
        }
        TextChannel channel = guild.getTextChannelById("951899628096344104");
        if (channel == null) {
            log.warn("Unable to find bug report channel!");
            return;
        }
        FEmbed embed = new EmbedBuilder()
                .title(Label.raw("New Bug Report"))
                .description(Label.raw(message))
                .footer(Label.raw(user.getAsTag() + " (" + user.getId() + ")"), user.getEffectiveAvatarUrl())
                .image(attachment)
                .build();
        this.msg.send(channel, FMessage.of(embed));
    }

}
