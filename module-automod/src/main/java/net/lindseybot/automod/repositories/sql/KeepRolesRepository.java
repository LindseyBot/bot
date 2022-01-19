package net.lindseybot.automod.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.KeepRoles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeepRolesRepository extends JpaRepository<KeepRoles, Long> {
}
