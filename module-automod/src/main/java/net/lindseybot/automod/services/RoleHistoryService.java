package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.automod.repositories.sql.KeepRolesRepository;
import net.lindseybot.automod.repositories.sql.RoleHistoryRepository;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.profile.members.MemberId;
import net.lindseybot.shared.entities.profile.members.RoleHistory;
import net.lindseybot.shared.entities.profile.servers.KeepRoles;
import net.lindseybot.shared.services.CacheService;
import net.lindseybot.shared.worker.services.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class RoleHistoryService {

    private final KeepRolesRepository repository;
    private final RoleHistoryRepository history;
    private final NotificationService notifications;
    private final CacheService cache;

    public RoleHistoryService(
            KeepRolesRepository repository,
            RoleHistoryRepository history,
            NotificationService notifications,
            CacheService cache) {
        this.repository = repository;
        this.history = history;
        this.notifications = notifications;
        this.cache = cache;
    }

    public boolean isActive(Guild guild) {
        if (this.cache.getRoleHistory().containsKey(guild.getIdLong())) {
            return this.cache.getRoleHistory().get(guild.getIdLong());
        }
        boolean active = this.repository.findById(guild.getIdLong())
                .orElse(new KeepRoles())
                .isEnabled();
        this.cache.getRoleHistory().put(guild.getIdLong(), active);
        return active;
    }

    public RoleHistory findByMember(Member member) {
        MemberId id = new MemberId();
        id.setUserId(member.getUser().getIdLong());
        id.setGuildId(member.getGuild().getIdLong());
        return this.history.findById(id)
                .orElse(null);
    }

    public void save(RoleHistory history) {
        this.history.save(history);
    }

    public void disable(Guild guild, Label keepRoles) {
        KeepRoles config = this.repository.findById(guild.getIdLong())
                .orElse(null);
        if (config == null) {
            return;
        }
        config.setEnabled(false);
        this.repository.save(config);
        this.cache.getRoleHistory().remove(guild.getIdLong());
        this.notifications.notify(guild, keepRoles);
    }

}
