package net.lindseybot.automod.listeners;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.automod.services.AntiScamService;
import net.lindseybot.shared.entities.profile.servers.AntiScam;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
public class AntiScamListener extends ListenerAdapter {

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.parse("application/json");

    private final AntiScamService service;
    private final ExpiringMap<Long, AtomicInteger> triggers;

    public AntiScamListener(AntiScamService service, IEventManager api) {
        this.service = service;
        api.register(this);
        this.triggers = ExpiringMap.builder()
                .maxSize(10_000)
                .expiration(15, TimeUnit.MINUTES)
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .build();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Member author = event.getMember();
        if (author == null
            || author.getUser().isBot()
            || author.hasPermission(Permission.MESSAGE_MANAGE)) {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContentRaw();
        Member self = event.getGuild().getSelfMember();
        if (!content.contains("http")) {
            return;
        } else if (!self.hasPermission(Permission.BAN_MEMBERS)
                   && (!self.hasPermission(Permission.MESSAGE_MANAGE)
                       || !self.hasPermission(event.getGuildChannel(), Permission.MESSAGE_MANAGE))) {
            return;
        } else if (!self.canInteract(event.getMember())) {
            return;
        }
        try {
            JSONObject data = this.fetchScam(message);
            if (data == null) {
                return;
            } else if (!data.has("match") || !data.getBoolean("match")) {
                return;
            }
            JSONArray matches = data.getJSONArray("matches");
            String domain = null;
            float max_score = 0;
            for (int i = 0; i < matches.length(); i++) {
                JSONObject object = matches.getJSONObject(i);
                float score = object.getFloat("trust_rating");
                if (score > max_score) {
                    max_score = score;
                    domain = object.getString("domain");
                }
            }
            if (max_score < 0.8) {
                return;
            }
            AntiScam settings = this.service.find(event.getGuild());
            if (!settings.isEnabled()) {
                return;
            }
            AtomicInteger integer = this.triggers.compute(event.getAuthor().getIdLong(), ((k, v) -> {
                if (v == null) {
                    return new AtomicInteger(0);
                }
                return v;
            }));
            if (integer.incrementAndGet() > settings.getStrikes()
                || OffsetDateTime.now().minusDays(1).isBefore(author.getTimeJoined())) {
                event.getMember().ban(24, TimeUnit.HOURS)
                        .reason("Scam link: " + domain)
                        .queue(this.noop(), this.noop());
                return;
            }
            message.delete()
                    .reason("Scam link")
                    .queue(this.noop(), this.noop());
        } catch (InsufficientPermissionException ex) {
            // Ignored
        } catch (Exception ex) {
            log.error("Failed to catch fish", ex);
        }
    }

    private JSONObject fetchScam(Message message) {
        JSONObject object = new JSONObject();
        object.put("message", message.getContentRaw());
        RequestBody body = RequestBody.create(JSON, object.toString());
        Request request = new Request.Builder().url("https://anti-fish.bitflow.dev/check")
                .header("User-Agent", "Lindsey/1.0 (https://lindseybot.net)")
                .post(body)
                .build();
        try (Response response = this.client.newCall(request).execute()) {
            ResponseBody resp = response.body();
            if (!response.isSuccessful() || resp == null) {
                return null;
            }
            return new JSONObject(resp.string());
        } catch (IOException e) {
            log.error("Failed to request to scamlink api");
            return null;
        }
    }

    private <T> Consumer<T> noop() {
        return t -> {
        };
    }

}
