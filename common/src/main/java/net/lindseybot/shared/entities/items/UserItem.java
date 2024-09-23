package net.lindseybot.shared.entities.items;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(UserItemId.class)
@Table(name = "user_items")
public class UserItem {

    @Id
    private long userId;

    @Id
    private long itemId;

    private long purchased;
    private int count;

    public UserItem(long userId, long itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

}
