package net.lindseybot.shared.entities.discord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.lindseybot.shared.utils.DiscordCDN;

import java.util.List;

@Data
public class FMember {

    private FUser user;

    private String nickname;
    private String avatarHash;
    private Boolean pending;
    private List<Long> roles;
    private String permissions;

    @JsonIgnore
    public String getAsMention() {
        return "<@" + (this.nickname == null ? "!" : "") + this.user.getId() + ">";
    }

    @JsonIgnore
    public String getEffectiveName() {
        return (this.nickname != null) ? this.nickname : this.user.getName();
    }

    @JsonIgnore
    public String getAvatarUrl() {
        if (this.avatarHash != null) {
            return DiscordCDN.avatarUrl(this.avatarHash, this.user.getId());
        } else if (this.user.getAvatarHash() != null) {
            return DiscordCDN.avatarUrl(this.user.getAvatarHash(), this.user.getId());
        } else {
            return DiscordCDN.defaultAvatar(this.user.getDiscriminator());
        }
    }

}
