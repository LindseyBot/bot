package net.lindseybot.shared.entities.profile.servers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "server_settings_registration")
public class Registration {

    @Id
    private long guild;

    private boolean enabled = false;
    private long channelId;
    private long roleId;
    private String phrase;

    public Registration(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Registration rg) {
            return Objects.equals(rg.guild, this.guild);
        }
        return false;
    }

}
