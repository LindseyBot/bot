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
public class Picarto extends InteractionHandler {

    private final OkHttpClient client = new OkHttpClient();

    public Picarto(Messenger msg) {
        super(msg);
    }

    @SlashCommand("picarto")
    public void onCommand(SlashCommandInteractionEvent event) {
        String name = this.getOption("name", event, String.class);
        Request request = new Request.Builder()
                .url("https://api.picarto.tv/v1/channel/name/" + name)
                .get().build();
        JSONObject obj;
        try (Response resp = client.newCall(request).execute(); ResponseBody body = resp.body()) {
            if (!resp.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("error.internal"));
                return;
            }
            obj = new JSONObject(body.string());
        } catch (IOException ex) {
            log.error("Failed to fetch streamer information", ex);
            this.msg.error(event, Label.of("error.internal"));
            return;
        }
        String nsfw = String.valueOf(obj.getBoolean("adult"));
        nsfw = nsfw.substring(0, 1).toUpperCase() + nsfw.substring(1).toLowerCase();
        if (nsfw.equals("True") && !event.getGuildChannel().asTextChannel().isNSFW()) {
            this.msg.error(event, Label.of("commands.streamers.nsfw"));
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        String title = obj.getString("name");
        embed.thumbnail(obj.getString("avatar"));
        if (obj.getBoolean("online")) {
            title = title + " - Online";
            embed.field(
                    Label.of("commands.streamers.title"),
                    Label.raw(obj.getString("title")),
                    true
            );
            embed.field(
                    Label.of("commands.streamers.category"),
                    Label.raw(obj.getString("category")),
                    true
            );
            embed.field(
                    Label.raw("NSFW"),
                    Label.raw(nsfw),
                    true
            );
            embed.field(
                    Label.of("commands.streamers.viewers"),
                    Label.raw(String.valueOf(obj.getInt("viewers"))),
                    true
            );
            embed.image(obj.getJSONObject("thumbnails").getString("web"));
            embed.color(Color.red);
        } else {
            embed.field(
                    Label.of("commands.streamers.last"),
                    Label.raw(obj.getString("last_live").substring(0, 9)),
                    true
            );
            embed.field(
                    Label.of("commands.streamers.category"),
                    Label.raw(obj.getString("category")),
                    true
            );
            embed.field(
                    Label.raw("NSFW"),
                    Label.raw(nsfw),
                    true
            );
            embed.field(
                    Label.of("commands.streamers.total"),
                    Label.raw(String.valueOf(obj.getInt("viewers_total"))),
                    true
            );
            embed.color(Color.gray);
        }
        embed.title(Label.raw(title));
        embed.url("https://picarto.tv/" + obj.getString("name"));
        embed.field(
                Label.of("commands.streamers.followers"),
                Label.raw(String.valueOf(obj.getInt("followers"))),
                true
        );
        embed.field(
                Label.of("commands.streamers.language"),
                Label.raw(obj.getJSONArray("languages").getJSONObject(0).getString("name")),
                true
        );
        embed.field(
                Label.of("commands.streamers.tags"),
                Label.raw(obj.getJSONArray("tags").toString()),
                true
        );
        embed.footer(Label.raw(event.getUser().getName()), event.getUser().getEffectiveAvatarUrl());
        this.msg.reply(event, FMessage.of(embed.build()));
    }

}
