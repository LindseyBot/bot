package net.lindseybot.help.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.AntiScam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpAntiScamRepository extends JpaRepository<AntiScam, Long> {
}
