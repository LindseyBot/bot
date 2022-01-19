package net.lindseybot.shared.entities.items;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

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

    private long count;

    public UserItem(long userId, long itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

}
