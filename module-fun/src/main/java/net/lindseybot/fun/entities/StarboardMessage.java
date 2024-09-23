package net.lindseybot.fun.entities;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "starboard")
public class StarboardMessage {

    @Id
    private long targetId;

    private Long messageId;
    private long guildId;
    private long channelId;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StarboardMessage msg) {
            return Objects.equals(msg.targetId, this.targetId);
        }
        return false;
    }

}
