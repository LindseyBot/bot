package net.lindseybot.automod.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.AntiAd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AntiAdRepository extends JpaRepository<AntiAd, Long> {
}
