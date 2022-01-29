package net.lindseybot.nsfw.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.ImageBoard;
import net.kodehawa.lib.imageboards.entities.BoardImage;
import net.kodehawa.lib.imageboards.entities.Rating;
import net.kodehawa.lib.imageboards.entities.impl.DanbooruImage;
import net.kodehawa.lib.imageboards.entities.impl.FurryImage;
import net.kodehawa.lib.imageboards.entities.impl.GelbooruImage;
import net.kodehawa.lib.imageboards.entities.impl.Rule34Image;
import net.lindseybot.nsfw.services.BooruService;
import net.lindseybot.shared.entities.discord.FEmbed;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Random;

@Slf4j
@Component
public class NSFW extends InteractionHandler {

    private final BooruService service;
    private final Random random = new Random();

    public NSFW(Messenger messenger, BooruService service) {
        super(messenger);
        this.service = service;
    }

    @SlashCommand(value = "nsfw.rule34", nsfw = true)
    public void onRule34(SlashCommandInteractionEvent event) {
        ImageBoard<Rule34Image> api = DefaultImageBoards.RULE34;
        String filter = this.getOption("tags", event, String.class);
        if (filter == null) {
            this.random(api, event);
        } else {
            this.search(api, event, filter);
        }
    }

    @SlashCommand(value = "nsfw.danbooru", nsfw = true)
    public void onDanbooru(SlashCommandInteractionEvent event) {
        ImageBoard<DanbooruImage> api = DefaultImageBoards.DANBOORU;
        String filter = this.getOption("tags", event, String.class);
        if (filter == null) {
            this.random(api, event);
        } else {
            this.search(api, event, filter);
        }
    }

    @SlashCommand(value = "nsfw.furry", nsfw = true)
    public void onFurry(SlashCommandInteractionEvent event) {
        ImageBoard<FurryImage> api = DefaultImageBoards.E621;
        String filter = this.getOption("tags", event, String.class);
        if (filter == null) {
            this.random(api, event);
        } else {
            this.search(api, event, filter);
        }
    }

    @SlashCommand(value = "nsfw.gelbooru", nsfw = true)
    public void onGelbooru(SlashCommandInteractionEvent event) {
        ImageBoard<GelbooruImage> api = DefaultImageBoards.GELBOORU;
        String filter = this.getOption("tags", event, String.class);
        if (filter == null) {
            this.random(api, event);
        } else {
            this.search(api, event, filter);
        }
    }

    private void random(ImageBoard<?> api, SlashCommandInteractionEvent event) {
        int page = Math.max(1, random.nextInt(25));
        api.get(page, 60, Rating.EXPLICIT).async(images -> {
            if (images == null) {
                this.msg.error(event, Label.of("commands.nsfw.api"));
                return;
            } else if (images.isEmpty()) {
                this.msg.error(event, Label.of("commands.nsfw.empty"));
                return;
            }
            BoardImage image = this.service.findOne(images);
            if (image == null) {
                this.msg.error(event, Label.of("commands.nsfw.empty"));
                return;
            }
            FEmbed embed = this.service.createEmbed(image);
            this.msg.reply(event, FMessage.of(embed));
        }, ex -> {
            this.msg.error(event, Label.of("error.internal"));
            log.error("Error fetching random image", ex);
        });
    }

    private void search(ImageBoard<?> api, SlashCommandInteractionEvent event, String filter) {
        api.search(60, filter, Rating.EXPLICIT).async(images -> {
            if (images == null) {
                this.msg.error(event, Label.of("commands.nsfw.api"));
                return;
            } else if (images.isEmpty()) {
                this.msg.error(event, Label.of("commands.nsfw.empty"));
                return;
            }
            Collections.shuffle(images);
            BoardImage image = this.service.findOne(images);
            if (image == null) {
                this.msg.error(event, Label.of("commands.nsfw.empty"));
                return;
            }
            FEmbed embed = this.service.createEmbed(image);
            this.msg.reply(event, FMessage.of(embed));
        }, ex -> {
            this.msg.error(event, Label.of("error.internal"));
            log.error("Error fetching tagged image", ex);
        });
    }

}
