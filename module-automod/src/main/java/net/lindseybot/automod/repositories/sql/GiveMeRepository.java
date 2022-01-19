package net.lindseybot.automod.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.GiveMe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiveMeRepository extends JpaRepository<GiveMe, Long> {
}
