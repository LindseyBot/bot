package net.lindseybot.help.repositories.sql;

import net.lindseybot.shared.entities.profile.servers.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpRegisterRepository extends JpaRepository<Registration, Long> {
}
