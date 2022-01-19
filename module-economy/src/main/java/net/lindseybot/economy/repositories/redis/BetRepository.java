package net.lindseybot.economy.repositories.redis;

import net.lindseybot.economy.models.BetModel;
import org.springframework.data.repository.CrudRepository;

public interface BetRepository extends CrudRepository<BetModel, Long> {
}
