package net.lindseybot.shared.entities.discord;

import lombok.Data;

@Data
public class FButton implements MessageComponent {

    private String idOrUrl;
    private String data;
    private Label label;
    private DiscordButtonStyle style;
    private boolean disabled;
    private FEmote emote;

}
