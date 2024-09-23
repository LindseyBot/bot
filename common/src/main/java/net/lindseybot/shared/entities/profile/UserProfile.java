package net.lindseybot.shared.entities.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.lindseybot.shared.enums.Flags;
import net.lindseybot.shared.enums.Language;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_settings")
public class UserProfile {

    @Id
    private long user;

    private String name;
    private long lastSeen;

    @Enumerated(EnumType.STRING)
    private Language language = Language.en_US;

    @Enumerated(EnumType.STRING)
    private Flags country;

    private long cookies = 0;
    private long slotWins = 0;

    private long cookieStreak = 0;
    private long lastDailyCookies = 0;

    public UserProfile(long user) {
        this.user = user;
    }

}
