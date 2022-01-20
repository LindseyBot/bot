package net.lindseybot.wiki.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class Define extends InteractionHandler {

    public Define(Messenger msg) {
        super(msg);
    }

    @SlashCommand("define")
    public void onCommand(SlashCommandEvent event) {
        String word = this.getOption("word", event, String.class);
        if (word == null) {
            return;
        }
        String definition = this.define(word);
        if (definition == null) {
            this.msg.reply(event, Label.of("commands.define.unknown", word));
        } else {
            this.msg.reply(event, Label.raw("> " + definition));
        }
    }

    private String define(String word) {
        StringBuilder builder = new StringBuilder("https://www.urbandictionary.com/define.php?term=");
        for (String s : word.split(" ")) {
            builder.append(URLEncoder.encode(s, StandardCharsets.UTF_8)).append("+");
        }
        builder.setLength(builder.length() - 1);
        try {
            Document d = Jsoup.connect(builder.toString()).followRedirects(true).get();
            Elements definitions = d.getElementsByClass("meaning");
            if (definitions.size() > 0) {
                String definition = definitions.get(0).text();
                if (definition.length() > 600) {
                    definition = definition.substring(0, 600) + "...";
                }
                if (!definition.endsWith(".")) {
                    definition += ".";
                }
                return definition;
            }
        } catch (HttpStatusException ex) {
            if (ex.getStatusCode() == 404 || ex.getStatusCode() == 500) {
                return null;
            }
            log.error("Error while defining term - HTTP " + ex.getStatusCode(), ex);
        } catch (Exception ex) {
            log.error("Error while defining term", ex);
        }
        return null;
    }

}
