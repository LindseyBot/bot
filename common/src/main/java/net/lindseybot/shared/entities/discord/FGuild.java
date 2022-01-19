package net.lindseybot.shared.entities.discord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.lindseybot.shared.utils.DiscordCDN;

import java.util.ArrayList;
import java.util.List;

@Data
public class FGuild {

    private long id;
    private String name;
    private String iconHash;

    private List<FChannel> channels;
    private List<FRole> roles;

    @JsonIgnore
    public String getIconUrl() {
        return DiscordCDN.guildUrl(this.iconHash, this.id);
    }

    @JsonIgnore
    public List<FChannel> getAllChannels() {
        List<FChannel> channels = new ArrayList<>();
        for (FChannel channel : this.channels) {
            if (channel instanceof FCategory) {
                channels.addAll(((FCategory) channel).getChannels());
            } else {
                channels.add(channel);
            }
        }
        return channels;
    }

    @JsonIgnore
    public FChannel getTextChannelById(long id) {
        return this.getAllChannels()
                .stream()
                .filter(channel -> channel.getType() == FChannelType.TEXT)
                .filter(channel -> channel.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @JsonIgnore
    public FChannel getVoiceChannelById(long id) {
        return this.getAllChannels()
                .stream()
                .filter(channel -> channel.getType() == FChannelType.VOICE)
                .filter(channel -> channel.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @JsonIgnore
    public FRole getRoleById(long id) {
        return this.roles.stream()
                .filter(role -> role.getId() == id)
                .findFirst()
                .orElse(null);
    }

}
