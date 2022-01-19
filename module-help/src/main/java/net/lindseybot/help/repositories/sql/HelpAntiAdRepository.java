package net.lindseybot.help.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.AntiAd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpAntiAdRepository extends JpaRepository<AntiAd, Long> {
}
