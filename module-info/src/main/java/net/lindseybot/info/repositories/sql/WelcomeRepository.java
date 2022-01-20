package net.lindseybot.info.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.Welcome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WelcomeRepository extends JpaRepository<Welcome, Long> {
}
