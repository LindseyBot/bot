package net.lindseybot.info.listeners;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.lindseybot.info.repositories.sql.WelcomeRepository;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.MentionType;
import net.lindseybot.shared.entities.profile.servers.Welcome;
import net.lindseybot.shared.utils.PlaceHolderUtils;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.NotificationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class WelcomeListener extends ListenerAdapter {

    private final Messenger msg;
    private final WelcomeRepository repository;
    private final NotificationService notifications;

    public WelcomeListener(ShardManager api, Messenger msg,
                           WelcomeRepository repository, NotificationService notifications) {
        this.msg = msg;
        this.repository = repository;
        this.notifications = notifications;
        api.addEventListener(this);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Optional<Welcome> oSettings = repository.findById(event.getGuild().getIdLong());
        if (oSettings.isEmpty()) {
            return;
        }
        Welcome settings = oSettings.get();
        if (!settings.isEnabled()) {
            return;
        }
        String msg = settings.getMessage();
        if (msg == null) {
            settings.setEnabled(false);
            repository.save(settings);
            this.notifications.notify(event.getGuild(), Label.of("logs.module.config", "Welcomer"));
            return;
        }
        String finalMsg = PlaceHolderUtils.replace(msg, event.getMember(), event.getGuild());
        TextChannel channel = event.getGuild()
                .getTextChannelById(settings.getChannelId());
        if (channel == null) {
            settings.setEnabled(false);
            repository.save(settings);
            this.notifications.notify(event.getGuild(), Label.of("logs.module.channel", "Welcomer"));
            return;
        } else if (!channel.canTalk()) {
            settings.setEnabled(false);
            repository.save(settings);
            this.notifications.notify(event.getGuild(), Label.of("logs.module.permission", "Welcomer"));
            return;
        }
        FMessage message = FMessage.of(Label.raw(finalMsg));
        message.setAllowedMentions(Arrays.asList(MentionType.EMOTE, MentionType.CHANNEL, MentionType.USER));
        this.msg.send(channel, message);
    }

}
