package net.lindseybot.bot.repositories.sql;

import net.lindseybot.shared.entities.profile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserProfile, Long> {

    int countByUser(long id);

}
