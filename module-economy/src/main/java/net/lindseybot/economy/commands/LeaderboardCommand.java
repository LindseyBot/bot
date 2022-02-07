package net.lindseybot.economy.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.lindseybot.economy.properties.ImageGenProperties;
import net.lindseybot.economy.repositories.sql.UserProfileRepository;
import net.lindseybot.economy.services.CustomizationService;
import net.lindseybot.shared.entities.discord.FAttachment;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.profile.UserProfile;
import net.lindseybot.shared.entities.profile.users.Customization;
import net.lindseybot.shared.enums.Flags;
import net.lindseybot.shared.enums.LeaderboardType;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@Component
public class LeaderboardCommand extends InteractionHandler {


    private final ShardManager jda;
    private final String endpoint;
    private final OkHttpClient client;
    private final UserProfileRepository repository;
    private final CustomizationService customizations;

    public LeaderboardCommand(Messenger msg, ShardManager jda,
                              UserProfileRepository repository,
                              ImageGenProperties properties,
                              CustomizationService customizations) {
        super(msg);
        this.jda = jda;
        this.repository = repository;
        this.client = new OkHttpClient();
        this.customizations = customizations;
        this.endpoint = "http://" + properties.getIp() + ":" + properties.getPort() + "/leaderboards";
    }

    @SlashCommand("leaderboard")
    public void onCommand(SlashCommandInteractionEvent event) {
        LeaderboardType type = LeaderboardType.fromString(
                this.getOption("name", event, String.class)
        );
        long rank = this.findRank(type, event.getUser().getIdLong());
        int pos = 0;
        JSONArray data = new JSONArray();
        for (UserProfile profile : this.getPage(type, rank < 11 ? 10 : 9)) {
            pos++;
            User user = this.getUser(profile.getUser());

            String name = profile.getName();
            if (name == null && user != null) {
                name = profile.getName();
            }

            String avatar = null;
            if (user != null) {
                avatar = user.getEffectiveAvatarUrl();
            }

            JSONObject object = this.getUserData(type, profile, pos, name, avatar);
            data.put(object);
        }
        if (rank > 10) {
            User user = event.getUser();
            UserProfile profile = this.repository.findById(user.getIdLong())
                    .orElse(new UserProfile());
            JSONObject object = this.getUserData(type, profile, rank, user.getName(), user.getEffectiveAvatarUrl());
            data.put(object);
        }
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), data.toString());
        Request request = new Request.Builder()
                .url(this.endpoint)
                .post(reqBody)
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute(); ResponseBody body = response.body()) {
            if (!response.isSuccessful() || body == null) {
                this.msg.error(event, Label.of("error.internal"));
                return;
            }
            byte[] bytes = body.bytes();
            FMessage message = new MessageBuilder()
                    .content(Label.raw("\n"))
                    .attach(new FAttachment("leaderboard.png", bytes))
                    .build();
            this.msg.reply(event, message);
        } catch (IOException ex) {
            log.error("Failed to request leaderboard", ex);
        }
    }

    private User getUser(long userId) {
        User user = this.jda.getUserById(userId);
        if (user != null) {
            return user;
        } else {
            return this.jda.retrieveUserById(userId)
                    .complete();
        }
    }

    private JSONObject getUserData(LeaderboardType type, UserProfile profile, long rank, String name, String avatar) {
        JSONObject object = new JSONObject();
        object.put("position", rank);
        object.put("username", name == null ? "Anonymous" : name);
        object.put("image", avatar);
        Customization customization = this.customizations.getCustomization(profile.getUser());
        if (customization.getBackground() != null) {
            JSONObject bg = new JSONObject();
            bg.put("id", customization.getBackground().getId());
            bg.put("fontColor", customization.getBackground().getFontColor());
            object.put("background", bg);
        }
        if (profile.getCountry() != null
                && profile.getCountry() != Flags.Unknown) {
            object.put("country", profile.getCountry().name().toLowerCase(Locale.ROOT));
        }
        object.put("value", this.getValue(type, profile));
        return object;
    }

    private String getValue(LeaderboardType type, UserProfile profile) {
        return "" + switch (type) {
            case COOKIES -> profile.getCookies();
            case SLOT_WINS -> profile.getSlotWins();
            case DAILY_STREAK -> profile.getCookieStreak();
        };
    }

    private Page<UserProfile> getPage(LeaderboardType type, int limit) {
        Sort sort = switch (type) {
            case COOKIES -> Sort.by(Sort.Direction.DESC, "cookies");
            case SLOT_WINS -> Sort.by(Sort.Direction.DESC, "slotWins");
            case DAILY_STREAK -> Sort.by(Sort.Direction.DESC, "cookieStreak");
        };
        Pageable pageable = PageRequest.of(0, limit, sort);
        return this.repository.findAll(pageable);
    }

    private long findRank(LeaderboardType type, long user) {
        if (type == LeaderboardType.COOKIES) {
            return this.repository.findCookieRank(user);
        } else if (type == LeaderboardType.SLOT_WINS) {
            return this.repository.findSlotRank(user);
        } else {
            return this.repository.findStreakRank(user);
        }
    }

}
