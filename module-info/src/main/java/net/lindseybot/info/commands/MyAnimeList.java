package net.lindseybot.info.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MyAnimeList extends InteractionHandler {

    private final OkHttpClient client = new OkHttpClient();

    protected MyAnimeList(Messenger msg) {
        super(msg);
    }

    @SlashCommand("mal")
    public void onCommand(SlashCommandEvent event) {
        String name = this.getOption("name", event, String.class);
        Request request = new Request.Builder()
                .url("https://api.jikan.moe/v3/search/anime?q=" + name)
                .get().build();
        JSONObject anime;
        try (Response response = client.newCall(request).execute(); ResponseBody body = response.body()) {
            if (!response.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("internal.error"));
                return;
            }
            JSONObject obj = new JSONObject(body.string());
            if (obj.getJSONArray("results").isEmpty()) {
                this.msg.error(event, Label.of("commands.kitsu.unknown"));
                return;
            }
            anime = obj.getJSONArray("results").getJSONObject(0);
        } catch (IOException ex) {
            log.error("Failed to fetch anime information", ex);
            this.msg.error(event, Label.of("internal.error"));
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        String age;
        if (!anime.isNull("rated")) {
            age = anime.getString("rated");
            embed.field(
                    Label.of("commands.kitsu.age"),
                    Label.raw(age),
                    true
            );
        } else {
            age = null;
        }
        if ("Rx".equals(age) && !event.getTextChannel().isNSFW()) {
            this.msg.error(event, Label.of("commands.kitsu.nsfw"));
            return;
        }
        embed.title(Label.raw(anime.getString("title")));
        embed.url(anime.getString("url"));
        if (!anime.isNull("synopsis")) {
            embed.description(Label.raw(anime.getString("synopsis")));
        }
        if (!anime.isNull("image_url")) {
            embed.thumbnail(anime.getString("image_url"));
        }
        if (!anime.isNull("type")) {
            embed.field(
                    Label.of("commands.mal.type"),
                    Label.raw(anime.getString("type")),
                    true
            );
        }
        if (!anime.isNull("airing")) {
            if (anime.getBoolean("airing")) {
                embed.field(
                        Label.of("commands.kitsu.status"),
                        Label.of("commands.mal.airing"),
                        true
                );
            } else {
                embed.field(
                        Label.of("commands.kitsu.status"),
                        Label.of("commands.mal.finished"),
                        true
                );
            }
        }
        if (!anime.isNull("episodes")) {
            embed.field(
                    Label.of("commands.kitsu.episodes"),
                    Label.raw(Integer.toString(anime.getInt("episodes"))),
                    true
            );
        }
        if (!anime.isNull("score")) {
            embed.field(
                    Label.of("commands.kitsu.rating"),
                    Label.raw(Integer.toString(anime.getInt("score"))),
                    true
            );
        }
        if (!anime.isNull("members")) {
            embed.field(
                    Label.of("commands.mal.members"),
                    Label.raw(Integer.toString(anime.getInt("members"))),
                    true
            );
        }
        if (!anime.isNull("start_date")) {
            embed.field(
                    Label.of("commands.kitsu.first"),
                    Label.raw(anime.getString("start_date").substring(0, 10)),
                    true
            );
        }
        if (!anime.isNull("end_date")) {
            embed.field(
                    Label.of("commands.kitsu.last"),
                    Label.raw(anime.getString("end_date").substring(0, 10)),
                    true
            );
        }
        embed.footer(Label.raw(event.getUser().getName()), event.getUser().getEffectiveAvatarUrl());
        this.msg.reply(event, FMessage.of(embed.build()));
    }

}
