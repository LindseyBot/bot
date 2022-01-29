package net.lindseybot.info.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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

import java.awt.*;
import java.io.IOException;

@Slf4j
@Component
public class Apoiase extends InteractionHandler {

    private final Color RED = new Color(210, 47, 47);
    private final Color GREEN = new Color(0, 204, 0);

    public Apoiase(Messenger msg) {
        super(msg);
    }

    @SlashCommand("apoiase")
    public void onCommand(SlashCommandInteractionEvent event) {
        OkHttpClient client = new OkHttpClient();
        JSONObject obj;
        try {
            String term = this.getOption("search", event, String.class);
            Request request = new Request.Builder()
                    .url("https://apoia.se/api/v1/users/campaign?page=0&q=" + term)
                    .get().build();
            Response response = client.newCall(request)
                    .execute();
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("error.internal"));
                return;
            }
            obj = new JSONObject(body.string());
            String slug;
            try {
                slug = obj.getJSONArray("campaigns").getJSONObject(0).getJSONObject("campaign").getString("slug");
            } catch (Exception e) {
                this.msg.error(event, Label.of("commands.crowdfunding.unknown"));
                return;
            }
            request = new Request.Builder()
                    .url("https://apoia.se/api/v1/users/" + slug)
                    .get().build();
            response = client.newCall(request).execute();
            body = response.body();
            if (!response.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("error.internal"));
                return;
            }
            obj = new JSONObject(body.string());
        } catch (IOException ex) {
            log.error("Failed to fetch information from apoia.se", ex);
            this.msg.error(event, Label.of("error.internal"));
            return;
        }
        JSONObject campaigns = obj.getJSONArray("campaigns").getJSONObject(0);
        JSONObject state = obj.getJSONArray("address").getJSONObject(0);
        boolean nsfw = campaigns.getBoolean("explicit");
        if (nsfw && !event.getTextChannel().isNSFW()) {
            this.msg.error(event, Label.of("commands.crowdfunding.nsfw"));
            return;
        }
        EmbedBuilder embed = new EmbedBuilder()
                .title(Label.raw(campaigns.getString("name")))
                .url("https://www.apoia.se/" + campaigns.getString("slug"))
                .footer(Label.raw(event.getUser().getName()), event.getUser().getEffectiveAvatarUrl())
                .image(campaigns.getJSONObject("about").getString("photo"))
                .field(
                        Label.of("commands.crowdfunding.category"),
                        Label.raw(campaigns.getJSONArray("categories").getString(0)), true
                ).field(
                        Label.of("commands.crowdfunding.creator"),
                        Label.raw(obj.getString("username")), true
                ).field(
                        Label.of("commands.crowdfunding.createdDate"),
                        Label.raw(campaigns.getString("createdDate").split("T")[0]), true
                ).field(
                        Label.of("commands.crowdfunding.city"),
                        Label.raw(state.getString("city") + " - " + state.getString("state")), true
                ).field(
                        Label.raw("NSFW"),
                        Label.raw(Boolean.toString(nsfw)), true
                );
        if (!campaigns.getJSONObject("about").isNull("slogan")) {
            embed.description(Label.raw(campaigns.getJSONObject("about").getString("slogan")));
        }
        if (!campaigns.getJSONArray("goals").isEmpty()) {
            embed.field(
                    Label.of("commands.crowdfunding.goal"),
                    Label.raw("R$" + campaigns.getJSONArray("goals").getJSONObject(0).getInt("value")),
                    true
            );
        }
        if (campaigns.getString("status").equals("published")) {
            embed.color(this.GREEN);
        } else {
            embed.color(this.RED);
        }
        this.msg.reply(event, FMessage.of(embed.build()));
    }

}
