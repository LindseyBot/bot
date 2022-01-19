package net.lindseybot.fun.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
