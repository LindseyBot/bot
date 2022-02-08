package net.lindseybot.economy.services;

import net.lindseybot.economy.repositories.sql.BadgeRepository;
import net.lindseybot.economy.repositories.sql.InventoryRepository;
import net.lindseybot.shared.entities.items.Item;
import net.lindseybot.shared.entities.items.UserItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final BadgeRepository badges;
    private final InventoryRepository repository;

    public InventoryService(BadgeRepository badges, InventoryRepository repository) {
        this.badges = badges;
        this.repository = repository;
    }

    public boolean hasItem(long userId, long itemId) {
        return this.repository.countByUserIdAndItemId(userId, itemId) > 0;
    }

    public List<UserItem> findBadges(long userId) {
        List<Long> ids = this.badges.findAll()
                .stream().map(Item::getId)
                .toList();
        return repository.findAllByUserIdAndItemIdIn(userId, ids);
    }

}
