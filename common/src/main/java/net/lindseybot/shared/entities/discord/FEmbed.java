package net.lindseybot.shared.entities.discord;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FEmbed {

    private String url;
    private Label title;
    private Integer color;
    private Label description;
    private Long timestamp;
    private String thumbnail;
    private String image;

    private Author author;
    private Footer footer;

    private List<Field> fields = new ArrayList<>();

    @Data
    public static class Field {

        private Label name;
        private Label value;
        private boolean inline;

    }

    @Data
    public static class Author {

        private Label name;
        private String url;
        private String icon;

    }

    @Data
    public static class Footer {

        private Label text;
        private String icon;

    }

}
