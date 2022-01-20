package net.lindseybot.wiki.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.wiki.properties.ApiProperties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Hearthstone extends InteractionHandler {

    private final OkHttpClient client = new OkHttpClient();
    private final ApiProperties properties;

    public Hearthstone(Messenger msg, ApiProperties properties) {
        super(msg);
        this.properties = properties;
    }

    @SlashCommand("hearthstone")
    public void onCommand(SlashCommandEvent event) {
        String card = this.getOption("card", event, String.class);
        boolean gold = Boolean.TRUE.equals(this.getOption("gold", event, Boolean.class));
        Request request = new Request.Builder()
                .url("https://omgvamp-hearthstone-v1.p.rapidapi.com/cards/search/" + card + "?collectible=1&locale=enUS")
                .get()
                .addHeader("x-rapidapi-host", "omgvamp-hearthstone-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", this.properties.getRapidApi())
                .build();
        String str;
        try (Response resp = client.newCall(request).execute(); ResponseBody body = resp.body()) {
            if (!resp.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("internal.error"));
                return;
            }
            str = body.string();
        } catch (IOException ex) {
            this.msg.error(event, Label.of("internal.error"));
            return;
        }
        String result = "";
        try {
            JSONArray arr = new JSONArray(str);
            if (gold) {
                result = arr.getJSONObject(0).getString("imgGold");
            } else {
                result = arr.getJSONObject(0).getString("img");
            }
        } catch (JSONException e) {
            JSONObject obj = new JSONObject(str);
            if (obj.getInt("error") == 404) {
                this.msg.error(event, Label.of("commands.hearthstone.unknown", card));
                return;
            }
        }
        this.msg.reply(event, Label.raw(result));
    }

}
