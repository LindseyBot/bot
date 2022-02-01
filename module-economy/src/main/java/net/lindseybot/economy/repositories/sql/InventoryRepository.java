package net.lindseybot.economy.repositories.sql;

import net.lindseybot.shared.entities.items.UserItem;
import net.lindseybot.shared.entities.items.UserItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<UserItem, UserItemId> {

    List<UserItem> findAllByUserIdAndItemIdIn(long user, List<Long> items);

    long countByUserIdAndItemId(long user, long item);

}
