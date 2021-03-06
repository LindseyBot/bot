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
@Table(name = "server_settings_antiad")
public class AntiAd {

    @Id
    private long guild;

    private boolean enabled = false;
    private int strikes = 3;

    public AntiAd(long guild) {
        this.guild = guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AntiAd ad) {
            return Objects.equals(ad.guild, this.guild);
        }
        return false;
    }

}
