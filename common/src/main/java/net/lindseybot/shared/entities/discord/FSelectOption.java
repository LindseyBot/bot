package net.lindseybot.shared.entities.discord;

import lombok.Data;

@Data
public class FSelectOption {

    private String id;
    private Label label;
    private Label description;
    private FEmote emote;
    private boolean isDefault;

}
