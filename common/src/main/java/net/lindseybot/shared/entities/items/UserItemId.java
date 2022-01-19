package net.lindseybot.shared.entities.items;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class UserItemId implements Serializable {

    @Id
    private long userId;

    @Id
    private long itemId;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserItemId id) {
            return Objects.equals(id.userId, this.userId)
                    && Objects.equals(id.itemId, this.itemId);
        }
        return false;
    }

}
