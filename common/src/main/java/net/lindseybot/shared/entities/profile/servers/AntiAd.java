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
