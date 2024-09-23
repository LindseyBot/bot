package net.lindseybot.shared.entities.profile.servers;

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
@Table(name = "server_settings_keeproles")
public class KeepRoles {

    @Id
    private long guild;

    private boolean enabled = false;

    public KeepRoles(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeepRoles kr) {
            return Objects.equals(kr.guild, this.guild);
        }
        return false;
    }

}
