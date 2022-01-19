package net.lindseybot.automod.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.lindseybot.automod.repositories.sql.KeepRolesRepository;
import net.lindseybot.automod.repositories.sql.RoleHistoryRepository;
import net.lindseybot.shared.entities.profile.members.MemberId;
import net.lindseybot.shared.entities.profile.members.RoleHistory;
import net.lindseybot.shared.entities.profile.servers.KeepRoles;
import org.springframework.stereotype.Service;

@Service
public class RoleHistoryService {

    private final KeepRolesRepository repository;
    private final RoleHistoryRepository history;

    public RoleHistoryService(KeepRolesRepository repository, RoleHistoryRepository history) {
        this.repository = repository;
        this.history = history;
    }


    public boolean isActive(Guild guild) {
        return this.repository.findById(guild.getIdLong())
                .orElse(new KeepRoles())
                .isEnabled();
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

}
