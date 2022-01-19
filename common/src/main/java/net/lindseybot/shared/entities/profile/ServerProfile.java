package net.lindseybot.shared.entities.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "server_settings")
public class ServerProfile {

    @Id
    private long guild;

    private long lastSeen;

    @Transient
    private Set<String> ignoredChannels;

    public ServerProfile(long guild) {
        this.guild = guild;
    }

}
