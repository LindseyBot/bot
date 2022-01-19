package net.lindseybot.shared.entities.discord.builders;

import net.lindseybot.shared.entities.discord.DiscordButtonStyle;
import net.lindseybot.shared.entities.discord.FButton;
import net.lindseybot.shared.entities.discord.FEmote;
import net.lindseybot.shared.entities.discord.Label;

public class ButtonBuilder {

    private final FButton button;

    public ButtonBuilder() {
        this.button = new FButton();
    }

    /**
     * Sets this button as disabled.
     *
     * @return Builder for chaining.
     */
    public ButtonBuilder disabled() {
        this.button.setDisabled(true);
        return this;
    }

    /**
     * Sets this button as disabled if the provided boolean is true.
     *
     * @return Builder for chaining.
     */
    public ButtonBuilder disabled(boolean disabled) {
        this.button.setDisabled(disabled);
        return this;
    }

    /**
     * Adds a small bit of custom data to a button.
     *
     * @param data Data to add.
     * @return Builder for chaining.
     */
    public ButtonBuilder withData(String data) {
        this.button.setData(data);
        return this;
    }

    /**
     * Sets an emote for this button.
     *
     * @param emote Emote.
     * @return Builder for chaining.
     */
    public ButtonBuilder withEmote(FEmote emote) {
        this.button.setEmote(emote);
        return this;
    }

    /**
     * Updates this button to be a primary button.
     *
     * @param id    Button ID.
     * @param label Button label.
     * @return Builder for chaining.
     */
    public ButtonBuilder primary(String id, Label label) {
        this.button.setIdOrUrl(id);
        this.button.setStyle(DiscordButtonStyle.PRIMARY);
        this.button.setLabel(label);
        return this;
    }

    /**
     * Updates this button to be a secondary button.
     *
     * @param id    Button ID.
     * @param label Button label.
     * @return Builder for chaining.
     */
    public ButtonBuilder secondary(String id, Label label) {
        this.button.setIdOrUrl(id);
        this.button.setStyle(DiscordButtonStyle.SECONDARY);
        this.button.setLabel(label);
        return this;
    }

    /**
     * Updates this button to be a success button.
     *
     * @param id    Button ID.
     * @param label Button label.
     * @return Builder for chaining.
     */
    public ButtonBuilder success(String id, Label label) {
        this.button.setIdOrUrl(id);
        this.button.setStyle(DiscordButtonStyle.SUCCESS);
        this.button.setLabel(label);
        return this;
    }

    /**
     * Updates this button to be a danger button.
     *
     * @param id    Button ID.
     * @param label Button label.
     * @return Builder for chaining.
     */
    public ButtonBuilder danger(String id, Label label) {
        this.button.setIdOrUrl(id);
        this.button.setStyle(DiscordButtonStyle.DANGER);
        this.button.setLabel(label);
        return this;
    }

    /**
     * Updates this button to be a link button.
     *
     * @param url   Button's link.
     * @param label Button label.
     * @return Builder for chaining.
     */
    public ButtonBuilder link(String url, Label label) {
        this.button.setIdOrUrl(url);
        this.button.setStyle(DiscordButtonStyle.LINK);
        this.button.setLabel(label);
        return this;
    }

    /**
     * Builds this button.
     *
     * @return Button.
     */
    public FButton build() {
        if (this.button.getStyle() == null) {
            throw new IllegalArgumentException("Missing button style");
        }
        return this.button;
    }

}
