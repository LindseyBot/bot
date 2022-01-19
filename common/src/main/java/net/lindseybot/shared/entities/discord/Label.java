package net.lindseybot.shared.entities.discord;

import lombok.Data;

@Data
public class Label {

    private boolean literal;
    private String name;
    private Object[] arguments;

    /**
     * Creates a translated label directly.
     *
     * @param i18n I18N key.
     * @param args Arguments if any.
     * @return Label.
     */
    public static Label of(String i18n, Object... args) {
        Label label = new Label();
        label.setLiteral(false);
        label.setName(i18n);
        label.setArguments(args);
        return label;
    }

    /**
     * Creates a raw label (bypass i18n).
     *
     * @param msg Label content.
     * @return Label.
     */
    public static Label raw(String msg) {
        Label label = new Label();
        label.setLiteral(true);
        label.setName(msg);
        return label;
    }

}
