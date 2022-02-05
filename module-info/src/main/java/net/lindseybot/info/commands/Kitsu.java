package net.lindseybot.info.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.lindseybot.info.properties.ApiProperties;
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
public class Kitsu extends InteractionHandler {

    private final OkHttpClient client = new OkHttpClient();
    private final ApiProperties properties;

    public Kitsu(Messenger msg, ApiProperties properties) {
        super(msg);
        this.properties = properties;
    }

    @SlashCommand("anime")
    public void onAnime(SlashCommandInteractionEvent event) {
        this.onCommand(event);
    }

    @SlashCommand("kitsu")
    public void onCommand(SlashCommandInteractionEvent event) {
        String name = this.getOption("name", event, String.class);
        Request request = new Request.Builder()
                .url("https://kitsu.io/api/edge/anime?filter[text]=" + name)
                .addHeader("Authorization", "Bearer " + this.properties.getKitsu())
                .get().build();
        String link;
        JSONObject data;
        try (Response response = client.newCall(request).execute(); ResponseBody body = response.body()) {
            if (!response.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("error.internal"));
                return;
            }
            JSONObject obj = new JSONObject(body.string());
            if (obj.getJSONArray("data").isEmpty()) {
                this.msg.error(event, Label.of("commands.kitsu.unknown"));
                return;
            }
            data = obj.getJSONArray("data").getJSONObject(0).getJSONObject("attributes");
            link = "https://kitsu.io/anime/" + obj.getJSONArray("data")
                    .getJSONObject(0)
                    .getJSONObject("links")
                    .getString("self")
                    .split("anime/")[1];
        } catch (IOException ex) {
            log.error("Failed to fetch anime data", ex);
            this.msg.error(event, Label.of("error.internal"));
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        boolean nsfw = false;
        if (!data.isNull("nsfw")) {
            if (data.getBoolean("nsfw")) {
                nsfw = true;
                embed.field(Label.raw("NSFW"), Label.raw("Yes"), true);
            } else {
                embed.field(Label.raw("NSFW"), Label.raw("No"), true);
            }
        }
        if (nsfw && !event.getTextChannel().isNSFW()) {
            this.msg.error(event, Label.of("commands.kitsu.nsfw"));
            return;
        }
        JSONObject titles = data.getJSONObject("titles");
        if (titles.has("en")) {
            if (titles.has("ja_jp")) {
                embed.title(Label.raw(titles.getString("en") + " - " + titles.getString("ja_jp")));
            } else {
                embed.title(Label.raw(titles.getString("en")));
            }
        } else if (titles.has("en_jp")) {
            if (titles.has("ja_jp")) {
                embed.title(Label.raw(titles.getString("en_jp") + " - " + titles.getString("ja_jp")));
            } else {
                embed.title(Label.raw(titles.getString("en_jp")));
            }
        } else if (titles.has("ja_jp")) {
            embed.title(Label.raw(titles.getString("ja_jp")));
        } else {
            log.warn("Anime " + name + " does not have a title.");
        }
        embed.url(link);
        embed.footer(Label.raw(event.getUser().getName()), event.getUser().getEffectiveAvatarUrl());
        if (!data.isNull("synopsis")) {
            embed.description(Label.raw(data.getString("synopsis")));
        }
        if (!data.getJSONObject("posterImage").isNull("original")) {
            embed.thumbnail(data.getJSONObject("posterImage").getString("original"));
        }
        if (!data.isNull("status")) {
            embed.field(
                    Label.of("commands.kitsu.status"),
                    Label.raw((data.getString("status")).toUpperCase()),
                    true
            );
        }
        if (!data.isNull("episodeCount")) {
            int eps = data.getInt("episodeCount");
            embed.field(
                    Label.of("commands.kitsu.episodes"),
                    Label.raw(String.valueOf(eps)),
                    true
            );
        }
        if (!data.isNull("averageRating")) {
            String rating = data.getString("averageRating");
            embed.field(
                    Label.of("commands.kitsu.rating"),
                    Label.raw(rating + " / 100"),
                    true
            );
        }
        if (!data.isNull("ratingRank")) {
            int rank = data.getInt("ratingRank");
            embed.field(
                    Label.of("commands.kitsu.rank"),
                    Label.raw(String.valueOf(rank)),
                    true
            );
        }
        if (!data.isNull("popularityRank")) {
            embed.field(
                    Label.of("commands.kitsu.popularity"),
                    Label.raw(String.valueOf(data.getInt("popularityRank"))),
                    true
            );
        }
        if (!data.isNull("startDate")) {
            embed.field(
                    Label.of("commands.kitsu.first"),
                    Label.raw(data.getString("startDate")),
                    true
            );
        }
        if (!data.isNull("endDate")) {
            embed.field(
                    Label.of("commands.kitsu.last"),
                    Label.raw(data.getString("endDate")),
                    true
            );
        }
        if (!data.isNull("nextRelease")) {
            embed.field(
                    Label.of("commands.kitsu.next"),
                    Label.raw(data.getString("nextRelease").substring(0, 10)),
                    true
            );
        }
        if (!data.isNull("ageRating") & !data.isNull("ageRatingGuide")) {
            embed.field(
                    Label.of("commands.kitsu.age"),
                    Label.raw(data.getString("ageRating") + " - " + data.getString("ageRatingGuide")),
                    true
            );
        }
        this.msg.reply(event, FMessage.of(embed.build()));
    }

}
