package net.lindseybot.automod.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.AntiScam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AntiScamRepository extends JpaRepository<AntiScam, Long> {
}
