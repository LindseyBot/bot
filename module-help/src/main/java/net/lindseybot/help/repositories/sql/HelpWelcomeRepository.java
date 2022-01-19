package net.lindseybot.help.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.Welcome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpWelcomeRepository extends JpaRepository<Welcome, Long> {
}
