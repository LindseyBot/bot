package net.lindseybot.automod.repositories.sql;

import net.lindseybot.shared.entities.profile.members.MemberId;
import net.lindseybot.shared.entities.profile.members.RoleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleHistoryRepository extends JpaRepository<RoleHistory, MemberId> {
}
