package net.lindseybot.bot.repositories.sql;

import net.lindseybot.entities.profile.ServerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<ServerProfile, Long> {

    int countByGuild(long id);

}
