package net.lindseybot.help.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.KeepRoles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpKeepRolesRepository extends JpaRepository<KeepRoles, Long> {
}
