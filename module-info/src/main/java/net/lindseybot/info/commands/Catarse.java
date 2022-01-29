package net.lindseybot.info.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.EmbedBuilder;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;

@Slf4j
@Component
public class Catarse extends InteractionHandler {

    private final OkHttpClient client = new OkHttpClient();
    private final Color RED = new Color(210, 47, 47);
    private final Color GREEN = new Color(0, 204, 0);

    public Catarse(Messenger msg) {
        super(msg);
    }

    @SlashCommand("catarse.user")
    public void onUser(SlashCommandInteractionEvent event) {
        JSONObject obj;
        try {
            String term = this.getOption("name", event, String.class);
            Request request = new Request.Builder()
                    .url("https://api.catarse.me/user_details?public_name=ilike." + term)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("error.internal"));
                return;
            }
            JSONArray array = new JSONArray(body.string());
            if (array.isEmpty()) {
                this.msg.error(event, Label.of("commands.catarse.user.unknown"));
                return;
            }
            obj = array.getJSONObject(0);
        } catch (IOException ex) {
            log.error("Failed to fetch user data", ex);
            this.msg.error(event, Label.of("error.internal"));
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        if (!obj.isNull("profile_img_thumbnail")) {
            embed.thumbnail(obj.getString("profile_img_thumbnail"));
        }
        embed.footer(Label.raw(event.getUser().getName()), event.getUser().getEffectiveAvatarUrl());
        embed.title(Label.raw(obj.getString("public_name"))).url("https://www.catarse.me/pt/users/" + obj.getInt("id"));
        embed.field(
                Label.of("commands.crowdfunding.contributed"),
                Label.raw(Integer.toString(obj.getInt("total_contributed_projects"))),
                true
        );
        embed.field(
                Label.of("commands.crowdfunding.published"),
                Label.raw(Integer.toString(obj.getInt("total_published_projects"))),
                true
        );
        embed.field(
                Label.of("commands.crowdfunding.created"),
                Label.raw(obj.getString("created_at").split("T")[0]),
                true
        );
        embed.field(
                Label.of("commands.crowdfunding.follows"),
                Label.raw(Integer.toString(obj.getInt("follows_count"))),
                true
        );
        embed.field(
                Label.of("commands.crowdfunding.followers"),
                Label.raw(Integer.toString(obj.getInt("followers_count"))),
                true
        );
        this.msg.reply(event, FMessage.of(embed.build()));
    }

    @SlashCommand("catarse.project")
    public void onProject(SlashCommandInteractionEvent event) {
        String term = this.getOption("name", event, String.class);
        if (term == null) {
            return;
        }
        RequestBody rBody = new FormBody.Builder()
                .add("query", term)
                .build();
        Request request = new Request.Builder()
                .url("https://api.catarse.me/rpc/project_search")
                .post(rBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        JSONObject obj;
        try (Response response = client.newCall(request).execute(); ResponseBody body = response.body()) {
            if (!response.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("error.internal"));
                return;
            }
            JSONArray array = new JSONArray(body.string());
            if (array.isEmpty()) {
                this.msg.error(event, Label.of("commands.catarse.project.unknown"));
                return;
            }
            obj = array.getJSONObject(0);
        } catch (IOException ex) {
            log.error("Failed to fetch user data", ex);
            this.msg.error(event, Label.of("error.internal"));
            return;
        }
        boolean nsfw = obj.getBoolean("is_adult_content");
        if (nsfw && !event.getTextChannel().isNSFW()) {
            this.msg.error(event, Label.of("commands.crowdfunding.nsfw"));
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.title(Label.of(obj.getString("project_name"), "https://www.catarse.me/" + obj.getString("permalink")));
        embed.footer(Label.raw(event.getUser().getName()), event.getUser().getEffectiveAvatarUrl());
        embed.image(obj.getString("project_img"));
        embed.field(Label.of("commands.wiki.crowdfunding.mode"), Label.raw(obj.getString("mode")), true);
        embed.field(Label.of("commands.wiki.crowdfunding.category"), Label.raw(obj.getString("category_name")), true);
        embed.field(Label.raw("NSFW"), Label.raw(Boolean.toString(obj.getBoolean("is_adult_content"))), true);
        embed.field(Label.of("commands.wiki.crowdfunding.creator"), Label.raw(obj.getString("owner_public_name")), true);
        if (obj.getBoolean("open_for_contributions")) {
            embed.color(Color.YELLOW);
        } else {
            if (obj.getFloat("progress") > 99.99) {
                embed.color(RED);
            } else {
                embed.color(GREEN);
            }
        }
        if (!obj.isNull("headline")) {
            embed.description(Label.raw(obj.getString("headline")));
        }
        if (!obj.isNull("pledged")) {
            embed.field(Label.of("commands.wiki.crowdfunding.pledged"), Label.raw("R$" + Math.round(obj.getFloat("pledged"))), true);
        }
        if (!obj.isNull("progress")) {
            embed.field(Label.of("commands.wiki.crowdfunding.progress"), Label.raw(obj.getInt("progress") + "%"), true);
        }
        if (!obj.getJSONObject("remaining_time").isNull("total") && obj.getBoolean("open_for_contributions")) {
            if (obj.getJSONObject("remaining_time").getInt("total") != 0) {
                embed.field(Label.of("commands.wiki.crowdfunding.remaining"), Label.raw(obj.getJSONObject("remaining_time").getInt("total") +
                        " " + obj.getJSONObject("remaining_time").getString("unit")), true);
            } else {
                embed.field(Label.of("commands.wiki.crowdfunding.remaining"), Label.raw("commands.wiki.crowdfunding.noDate"), true);
            }
        }
        if (!obj.getJSONObject("elapsed_time").isNull("total")) {
            embed.field(Label.of("commands.wiki.crowdfunding.elapsed"), Label.raw(obj.getJSONObject("elapsed_time").getInt("total") +
                    " " + obj.getJSONObject("elapsed_time").getString("unit")), true);
        }
        embed.field(Label.of("commands.wiki.crowdfunding.city"), Label.raw(obj.getString("city_name")), true);
        this.msg.reply(event, FMessage.of(embed.build()));
    }
}
