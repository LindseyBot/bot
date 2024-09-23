package net.lindseybot.shared.entities.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
