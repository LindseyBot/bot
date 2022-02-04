package net.lindseybot.shared.entities.discord.builders;

import net.lindseybot.shared.entities.discord.FSelectMenu;
import net.lindseybot.shared.entities.discord.FSelectOption;
import net.lindseybot.shared.entities.discord.Label;

public class SelectMenuBuilder {

    private final FSelectMenu menu;

    public SelectMenuBuilder(String id) {
        menu = new FSelectMenu();
        menu.setId(id);
    }

    public SelectMenuBuilder withLabel(Label label) {
        this.menu.setLabel(label);
        return this;
    }

    public SelectMenuBuilder withRange(int min, int max) {
        this.menu.setMin(min);
        this.menu.setMax(max);
        return this;
    }

    public SelectMenuBuilder disabled() {
        this.menu.setDisabled(true);
        return this;
    }

    public SelectMenuBuilder addOption(FSelectOption... options) {
        for (FSelectOption option : options) {
            this.menu.getOptions().add(option);
        }
        return this;
    }

    public SelectMenuBuilder addOption(SelectOptionBuilder... options) {
        for (SelectOptionBuilder option : options) {
            this.menu.getOptions().add(option.build());
        }
        return this;
    }

    public SelectMenuBuilder withData(String data) {
        this.menu.setData(data);
        return this;
    }

    public FSelectMenu build() {
        return this.menu;
    }

}
