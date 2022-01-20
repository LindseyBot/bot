package net.lindseybot.wiki.commands;

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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Pokedex extends InteractionHandler {

    public Pokedex(Messenger msg) {
        super(msg);
    }

    @SlashCommand("pokedex")
    public void onCommand(SlashCommandEvent event) {
        String search = this.getOption("search", event, String.class);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://pokeapi.co/api/v2/pokemon/" + search)
                .get().build();
        String str;
        try (Response resp = client.newCall(request).execute(); ResponseBody body = resp.body();) {
            if (body == null || !resp.isSuccessful()) {
                this.msg.error(event, Label.of("commands.pokedex.unknown"));
                return;
            }
            str = body.string();
            if (str.equals("Not Found")) {
                this.msg.error(event, Label.of("commands.pokedex.unknown"));
                return;
            }
        } catch (IOException ex) {
            this.msg.error(event, Label.of("internal.error"));
            return;
        }
        JSONObject obj = new JSONObject(str);
        String name = obj.getString("name");
        EmbedBuilder embed = new EmbedBuilder()
                .title(Label.raw(name.substring(0, 1).toUpperCase() + name.substring(1)))
                .field(Label.of("commands.pokedex.id"), Label.raw(Integer.toString(obj.getInt("id"))), true)
                .field(Label.of("commands.pokedex.height"), Label.raw(Double.toString((double) obj.getInt("height") / 10)), true)
                .field(Label.of("commands.pokedex.weight"), Label.raw(Double.toString((double) obj.getInt("weight") / 10)), true)
                .thumbnail(obj.getJSONObject("sprites").getString("front_default"))
                .footer(Label.raw(event.getUser().getName()), event.getUser().getEffectiveAvatarUrl());
        JSONArray types = obj.getJSONArray("types");
        if (types.length() == 1) {
            embed.field(
                    Label.of("commands.pokedex.type"),
                    Label.raw(types.getJSONObject(0).getJSONObject("type").getString("name")),
                    true
            );
        } else {
            embed.field(
                    Label.of("commands.pokedex.type"),
                    Label.raw(types.getJSONObject(0).getJSONObject("type").getString("name") + " & " +
                            types.getJSONObject(1).getJSONObject("type").getString("name")),
                    true
            );
        }
        this.msg.reply(event, FMessage.of(embed.build()));
    }

}
