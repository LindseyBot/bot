package net.lindseybot.fun.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.Starboard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarboardRepository extends JpaRepository<Starboard, Long> {

}
