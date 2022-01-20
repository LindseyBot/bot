package net.lindseybot.nsfw.services;

import net.kodehawa.lib.imageboards.ImageBoard;
import net.kodehawa.lib.imageboards.entities.BoardImage;
import net.lindseybot.shared.entities.discord.FEmbed;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BooruService {

    private final Set<String> banned = new HashSet<>();

    public BooruService() {
        ImageBoard.setUserAgent("Lindsey/1.0 (https://lindseybot.net/)");
        banned.add("cub");
        banned.add("loli");
        banned.add("shota");
        banned.add("young");
        banned.add("child");
        banned.add("childish");
        banned.add("kid");
        banned.add("kids");
        banned.add("bestiality");
    }

    public boolean isRisky(BoardImage image) {
        if (image.isPending()) {
            return true;
        }
        List<String> tags = image.getTags();
        if (tags == null) {
            return true;
        }
        return tags.stream().map(String::toLowerCase).anyMatch(this.banned::contains);
    }

    public BoardImage findOne(List<? extends BoardImage> images) {
        return images.stream()
                .filter(img -> !this.isRisky(img))
                .findAny()
                .orElse(null);
    }

    public FEmbed createEmbed(BoardImage image) {
        List<String> tags = image.getTags();
        if (tags == null || tags.isEmpty()) {
            throw new IllegalStateException("Invalid image");
        }
        String tagString = String.join(", ", tags).replace("_", "\\_");
        if (tagString.length() > 256) {
            tagString = tagString.substring(0, 256) + "...";
        }
        String time = "<t:" + image.getCreationMillis() / 1000 + ":R>";
        return new EmbedBuilder()
                .title(Label.of("commands.nsfw.title", image.getRating().name()))
                .url(image.getURL())
                .field(Label.of("commands.nsfw.height"), Label.raw(image.getHeight() + "px"), true)
                .field(Label.of("commands.nsfw.width"), Label.raw(image.getWidth() + "px"), true)
                .field(Label.of("commands.nsfw.posted"), Label.raw(time), true)
                .description(Label.of("commands.nsfw.tags", tagString))
                .image(image.getURL())
                .build();
    }

}
