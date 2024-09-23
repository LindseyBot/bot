package net.lindseybot.shared.entities.profile.members;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class MemberId implements Serializable {

    @Id
    private long userId;

    @Id
    private long guildId;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MemberId id) {
            return Objects.equals(id.userId, this.userId)
                    && Objects.equals(id.guildId, this.guildId);
        }
        return false;
    }

}
