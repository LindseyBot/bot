package net.lindseybot.bot.listener;

import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.lindseybot.bot.services.ProfileServiceImpl;
import net.lindseybot.shared.entities.profile.ServerProfile;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class LastSeenListener extends ListenerAdapter {

    private final ProfileServiceImpl service;
    private final Set<Long> pending = new HashSet<>();

    public LastSeenListener(IEventManager api, ProfileServiceImpl service) {
        this.service = service;
        api.register(this);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        if (!this.service.has(event.getGuild())) {
            ServerProfile profile = new ServerProfile();
            profile.setGuild(event.getGuild().getIdLong());
            profile.setLastSeen(System.currentTimeMillis());
            this.service.save(profile);
        } else {
            this.onSeen(event.getGuild());
        }
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        if (!this.service.has(event.getGuild())) {
            ServerProfile profile = new ServerProfile();
            profile.setGuild(event.getGuild().getIdLong());
            profile.setLastSeen(System.currentTimeMillis());
            this.service.save(profile);
        } else {
            this.onSeen(event.getGuild());
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        this.onSeen(event.getGuild());
    }

    @PreDestroy
    protected void onShutdown() {
        if (this.pending.isEmpty()) {
            return;
        }
        this.service.updateSeen(this.pending);
        this.pending.clear();
    }

    private synchronized void onSeen(Guild guild) {
        this.pending.add(guild.getIdLong());
        if (this.pending.size() > 500) {
            this.service.updateSeen(this.pending);
            this.pending.clear();
        }
    }

}
