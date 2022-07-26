package net.lindseybot.automod.listeners;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.lindseybot.automod.services.RoleHistoryService;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.profile.members.RoleHistory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class RoleHistoryListener extends ListenerAdapter {

    private final RoleHistoryService service;

    public RoleHistoryListener(IEventManager api, RoleHistoryService service) {
        this.service = service;
        api.register(this);
    }

    @Override
    public void onGuildMemberUpdate(@NotNull GuildMemberUpdateEvent event) {
        if (!this.service.isActive(event.getGuild())) {
            return;
        }
        this.saveRoles(event.getMember());
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (event.getMember() == null) {
            return;
        } else if (!this.service.isActive(event.getGuild())) {
            return;
        }
        this.saveRoles(event.getMember());
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        boolean isToKeepRoles = this.service.isActive(event.getGuild());
        if (!isToKeepRoles) {
            return;
        }
        RoleHistory history = this.service.findByMember(event.getMember());
        if (history == null) {
            return;
        }
        try {
            Set<Role> roles = history.getRoles().stream()
                    .map(roleId -> event.getGuild().getRoleById(roleId))
                    .filter(Objects::nonNull)
                    .filter(role -> !role.isManaged())
                    .filter(role -> event.getGuild().getSelfMember().canInteract(role))
                    .collect(Collectors.toSet());
            event.getGuild().modifyMemberRoles(event.getMember(), roles)
                    .reason("KeepRoles")
                    .queue(noop(), noop());
        } catch (InsufficientPermissionException ex) {
            this.service.disable(event.getGuild(), Label.of("logs.module.permission", "KeepRoles"));
        }
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

    private void saveRoles(Member member) {
        if (member.getUser().isBot()) {
            return;
        }
        Set<Long> roles = member.getRoles()
                .stream()
                .map(ISnowflake::getIdLong)
                .collect(Collectors.toSet());
        RoleHistory history = this.service.findByMember(member);
        if (history == null) {
            history = new RoleHistory();
            history.setUserId(member.getUser().getIdLong());
            history.setGuildId(member.getGuild().getIdLong());
        }
        if (history.getRoles() == null) {
            history.setRoles(new HashSet<>());
        } else {
            history.getRoles().clear();
        }
        history.getRoles().addAll(roles);
        history.setLastUpdated(System.currentTimeMillis());
        this.service.save(history);
    }

}
