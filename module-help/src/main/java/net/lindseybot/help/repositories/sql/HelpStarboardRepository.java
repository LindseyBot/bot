package net.lindseybot.help.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.Starboard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpStarboardRepository extends JpaRepository<Starboard, Long> {
}
