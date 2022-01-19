package net.lindseybot.economy.repositories.redis;


import net.lindseybot.economy.models.BlackjackModel;
import org.springframework.data.repository.CrudRepository;

public interface BlackjackRepository extends CrudRepository<BlackjackModel, Long> {
}
