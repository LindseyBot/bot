package net.lindseybot.fun.repositories.sql;

import net.lindseybot.fun.entities.StarboardMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StarboardMessageRepository extends JpaRepository<StarboardMessage, Long> {

    Optional<StarboardMessage> findByMessageId(long starboard);

}
