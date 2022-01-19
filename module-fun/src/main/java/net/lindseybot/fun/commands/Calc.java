package net.lindseybot.fun.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Calc extends InteractionHandler {

    private final OkHttpClient client = new OkHttpClient();

    protected Calc(Messenger msg) {
        super(msg);
    }

    @SlashCommand("calc")
    public void onCommand(SlashCommandEvent event) {
        String expr = this.getOption("expression", event, String.class);
        if (expr == null) {
            return;
        }
        JSONArray expression = new JSONArray();
        if (expr.contains(",")) {
            String[] expressions = expr.split(",");
            for (String item : expressions) {
                expression.put(item.trim());
            }
        } else {
            expression.put(expr);
        }
        JSONObject object = new JSONObject();
        object.put("expr", expression);
        object.put("precision", 14);
        Request request = new Request.Builder()
                .url("https://api.mathjs.org/v4/")
                .post(RequestBody.create(object.toString(), MediaType.parse("application/json")))
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();
        JSONObject data;
        try (Response response = this.client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("No body");
            }
            data = new JSONObject(body.string());
        } catch (IOException ex) {
            log.error("Failed to execute math expression", ex);
            this.msg.error(event, Label.of("internal.error"));
            return;
        }
        if (!data.isNull("error")) {
            this.msg.error(event, Label.raw(data.getString("error")));
            return;
        }
        JSONArray result = data.getJSONArray("result");
        StringBuilder message = new StringBuilder();
        if (expr.contains(",")) {
            for (int i = 0; i < result.length(); i++) {
                String right = result.getString(i);
                String left = expression.getString(i);
                if (left.contains("=")) {
                    left = left.split("=")[0].trim();
                }
                message.append("\n`")
                        .append(left).append(" = ").append(right)
                        .append("`");
            }
        } else {
            message.append("`")
                    .append(result.getString(0))
                    .append("`");
        }
        this.msg.reply(event, Label.raw(message.toString().trim()));
    }

}
