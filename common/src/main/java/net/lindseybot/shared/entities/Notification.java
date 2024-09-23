package net.lindseybot.shared.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    private long id;

    private long guildId;
    private long userId;

    private long timestamp;
    private String message;

    public Notification(long id, String message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Notification nt) {
            return Objects.equals(nt.id, this.id);
        }
        return false;
    }

}
