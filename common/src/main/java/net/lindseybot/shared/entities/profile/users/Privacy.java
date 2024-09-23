package net.lindseybot.shared.entities.profile.users;

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
@Table(name = "user_settings_privacy")
public class Privacy {

    @Id
    private long user;

    private boolean anonymousInLeaderboards = false;

    public Privacy(long user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Privacy up) {
            return Objects.equals(up.user, this.user);
        }
        return false;
    }

}
