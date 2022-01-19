package net.lindseybot.shared.entities.discord.builders;

import net.lindseybot.shared.entities.discord.FEmote;
import net.lindseybot.shared.entities.discord.FSelectOption;
import net.lindseybot.shared.entities.discord.Label;

public class SelectOptionBuilder {

    private final FSelectOption option;

    public SelectOptionBuilder(String id, Label label) {
        option = new FSelectOption();
        option.setId(id);
        option.setLabel(label);
    }

    public SelectOptionBuilder asDefault() {
        this.option.setDefault(true);
        return this;
    }

    public SelectOptionBuilder withDescription(Label label) {
        this.option.setDescription(label);
        return this;
    }

    public SelectOptionBuilder withEmote(FEmote emote) {
        this.option.setEmote(emote);
        return this;
    }

    public FSelectOption build() {
        return this.option;
    }

}
