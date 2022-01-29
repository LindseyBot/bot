package net.lindseybot.info.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;

@Slf4j
@Component
public class Twitch extends InteractionHandler {

    private final OkHttpClient client = new OkHttpClient();
    private final ApiProperties properties;
    private JSONArray array;
    private final Color PURPLE = new Color(145, 70, 255);

    public Twitch(Messenger msg, ApiProperties properties) {
        super(msg);
        this.properties = properties;
    }

    @SlashCommand("twitch")
    public void onCommand(SlashCommandInteractionEvent event) {
        String query = this.getOption("name", event, String.class);
        Request request = new Request.Builder()
                .url("https://api.twitch.tv/helix/streams?user_login=" + query)
                .addHeader("Client-Id", properties.getTwitchClient())
                .addHeader("Authorization", "Bearer " + properties.getTwitchKey())
                .get().build();
        try (Response resp = client.newCall(request).execute(); ResponseBody body = resp.body()) {
            if (!resp.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("error.internal"));
                return;
            }
            array = new JSONObject(body.string()).getJSONArray("data");
        } catch (IOException ex) {
            log.error("Failed to fetch streamer information", ex);
            this.msg.error(event, Label.of("error.internal"));
            return;
        }
        FMessage message;
        if (array.isEmpty()) {
            message = getByChannel(event.getUser(), query);
        } else {
            message = getByLogin(event.getUser());
        }
        this.msg.reply(event, message);
    }

    public FMessage getByLogin(User user) {
        JSONObject obj = array.getJSONObject(0);
        String name = obj.getString("user_name");
        EmbedBuilder embed = new EmbedBuilder()
                .title(Label.raw(name)).url("https://www.twitch.tv/" + name)
                .image(obj.getString("thumbnail_url").replace("{width}", "840").replace("{height}", "480"))
                .field(Label.of("commands.streamers.playing"), Label.raw(obj.getString("game_name")), true)
                .field(Label.of("commands.streamers.language"), Label.raw(obj.getString("language")), true)
                .field(Label.of("commands.streamers.viewers"), Label.raw(String.valueOf(obj.getInt("viewer_count"))), true)
                .field(Label.of("commands.streamers.title"), Label.raw(obj.getString("title")), true)
                .color(PURPLE)
                .footer(Label.raw(user.getName()), user.getEffectiveAvatarUrl());
        return FMessage.of(embed.build());
    }

    public FMessage getByChannel(User user, String query) {
        Request request = new Request.Builder()
                .url("https://api.twitch.tv/helix/search/channels?first=1&query=" + query)
                .addHeader("Client-Id", properties.getTwitchClient())
                .addHeader("Authorization", "Bearer " + properties.getTwitchKey())
                .get().build();
        JSONObject obj;
        try (Response resp = client.newCall(request).execute(); ResponseBody body = resp.body()) {
            if (!resp.isSuccessful() || body == null) {
                return FMessage.of(Label.of("error.internal"), true);
            }
            obj = new JSONObject(body.string()).getJSONArray("data").getJSONObject(0);
        } catch (IOException ex) {
            log.error("Failed to fetch streamer information", ex);
            return FMessage.of(Label.of("error.internal"), true);
        }
        String name = obj.getString("display_name");
        EmbedBuilder embed = new EmbedBuilder()
                .title(Label.raw(name)).url("https://www.twitch.tv/" + name)
                .thumbnail(obj.getString("thumbnail_url"))
                .field(Label.of("commands.streamers.online"), Label.raw(String.valueOf(obj.getBoolean("is_live"))), true)
                .field(Label.of("commands.streamers.language"), Label.raw(obj.getString("broadcaster_language")), true)
                .field(Label.of("commands.streamers.title"), Label.raw(obj.getString("title")), false)
                .color(Color.gray)
                .footer(Label.raw(user.getName()), user.getEffectiveAvatarUrl());
        return FMessage.of(embed.build());
    }

}
