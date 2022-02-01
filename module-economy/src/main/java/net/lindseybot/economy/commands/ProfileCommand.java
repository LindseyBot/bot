package net.lindseybot.economy.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.lindseybot.economy.properties.ImageGenProperties;
import net.lindseybot.economy.repositories.sql.BadgeRepository;
import net.lindseybot.economy.repositories.sql.InventoryRepository;
import net.lindseybot.economy.services.CustomizationService;
import net.lindseybot.shared.entities.discord.FAttachment;
import net.lindseybot.shared.entities.discord.FMessage;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.items.Item;
import net.lindseybot.shared.entities.items.UserItem;
import net.lindseybot.shared.entities.profile.UserProfile;
import net.lindseybot.shared.entities.profile.users.Customization;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.ProfileService;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class ProfileCommand extends InteractionHandler {

    private final String endpoint;
    private final OkHttpClient client;
    private final ProfileService profiles;
    private final CustomizationService customizations;
    private final InventoryRepository items;
    private final BadgeRepository badges;

    public ProfileCommand(Messenger msg,
                          CustomizationService customizations,
                          ImageGenProperties properties,
                          ProfileService profiles,
                          InventoryRepository items,
                          BadgeRepository badges) {
        super(msg);
        this.customizations = customizations;
        this.profiles = profiles;
        this.items = items;
        this.badges = badges;
        this.client = new OkHttpClient();
        this.endpoint = "http://" + properties.getIp() + ":" + properties.getPort() + "/images/profile";
    }

    @SlashCommand("profile")
    public void onProfile(SlashCommandInteractionEvent event) {
        User target = this.getOption("target", event, User.class);
        if (target == null) {
            target = event.getUser();
        }
        UserProfile profile = profiles.get(target);

        JSONObject object = new JSONObject();
        object.put("name", target.getName());
        object.put("image", target.getEffectiveAvatarUrl());
        object.put("cookies", profile.getCookies());

        Customization customization = this.customizations.getCustomization(target.getIdLong());
        if (customization.getBackground() != null) {
            JSONObject background = new JSONObject();
            background.put("id", customization.getBackground().getId());
            background.put("fontColor", customization.getBackground().getFontColor());
            object.put("background", background);
        }
        if (profile.getCountry() != null) {
            object.put("country", profile.getCountry().name().toLowerCase(Locale.ROOT));
        }

        List<Long> ids = this.badges.findAll()
                .stream().map(Item::getId).toList();
        List<Long> badges = this.items.findAllByUserIdAndItemIdIn(target.getIdLong(), ids)
                .stream().map(UserItem::getItemId).toList();
        object.put("badges", badges);

        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), object.toString());
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
                    .attach(new FAttachment("profile.png", bytes))
                    .build();
            this.msg.reply(event, message);
        } catch (IOException ex) {
            log.error("Failed to request profile", ex);
        }
    }

}
