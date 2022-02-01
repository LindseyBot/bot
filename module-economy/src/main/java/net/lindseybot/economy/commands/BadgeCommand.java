package net.lindseybot.economy.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.lindseybot.economy.properties.ImageGenProperties;
import net.lindseybot.economy.repositories.sql.BadgeRepository;
import net.lindseybot.economy.services.InventoryService;
import net.lindseybot.shared.entities.items.Badge;
import net.lindseybot.shared.entities.items.UserItem;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.Translator;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class BadgeCommand extends InteractionHandler {

    private final Translator i18n;
    private final String endpoint;
    private final BadgeRepository badges;
    private final InventoryService inventory;
    private final OkHttpClient client = new OkHttpClient();

    public BadgeCommand(Messenger msg,
                        Translator i18n, ImageGenProperties properties,
                        BadgeRepository badges,
                        InventoryService inventory) {
        super(msg);
        this.i18n = i18n;
        this.endpoint = "http://" + properties.getIp() + ":" + properties.getPort() + "/badges";
        this.badges = badges;
        this.inventory = inventory;
    }

    @SlashCommand("badge.list")
    public void onList(SlashCommandInteractionEvent event) {
        List<Badge> badges = this.badges.findAll();
        // --
        Message message = new MessageBuilder()
                .setContent("\n")
                .setActionRows(ActionRow.of(
                        Button.secondary("badge.next:1", "Next")
                )).build();
        // --
        Badge badge = badges.get(0);
        byte[] info = this.getInfo(event.getUser(), badge, this.inventory.hasBadge(event.getUser().getIdLong(), badge.getId()));
        if (event.isAcknowledged()) {
            event.getHook().editOriginal(message)
                    .addFile(info, "badge.png")
                    .queue();
        } else {
            event.reply(message)
                    .addFile(info, "badge.png")
                    .queue();
        }
    }

    @net.lindseybot.shared.worker.Button("badge.next")
    public void onNext(ButtonInteractionEvent event) {
        int next = Integer.parseInt(this.getData(event));
        // --

        boolean exhausted;
        List<ItemComponent> items = new ArrayList<>();
        List<Badge> badges = this.badges.findAll();
        if (next > 0) {
            items.add(Button.secondary("badge.next:" + (next - 1), "Previous"));
        }
        if ((next + 1) < badges.size()) {
            items.add(Button.secondary("badge.next:" + (next + 1), "Next"));
        }

        // --
        Message message = new MessageBuilder()
                .setContent("\n")
                .setActionRows(ActionRow.of(
                        items.toArray(new ItemComponent[0])
                )).build();
        // --
        Badge badge = badges.get(next);
        byte[] info = this.getInfo(
                event.getUser(), badge, this.inventory.hasBadge(event.getUser().getIdLong(), badge.getId())
        );
        if (event.isAcknowledged()) {
            event.getHook().editOriginal(message)
                    .retainFiles(Collections.emptyList())
                    .addFile(info, "badge.png")
                    .queue();
        } else {
            event.editMessage(message)
                    .retainFiles()
                    .addFile(info, "badge.png")
                    .queue();
        }
    }

    @SlashCommand("badge.equip")
    public void onEquip(SlashCommandInteractionEvent event) {

    }

    @SlashCommand("badge.info")
    public void onInfo(SlashCommandInteractionEvent event) {
        List<UserItem> items = inventory.findBadges(event.getUser().getIdLong());
    }

    public byte[] getInfo(User user, Badge badge, boolean hasItem) {
        JSONObject object = new JSONObject();
        object.put("id", badge.getId());
        object.put("name", badge.getName());
        object.put("description", this.i18n.get(user, badge.getDescription()));
        object.put("unlocked", hasItem ? "2022-01-31" : null);

        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), object.toString());
        Request request = new Request.Builder()
                .url(this.endpoint)
                .post(reqBody)
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute(); ResponseBody body = response.body()) {
            if (!response.isSuccessful() || body == null) {
                return null;
            }
            return body.bytes();
        } catch (IOException ex) {
            log.error("Failed to request profile", ex);
            return null;
        }
    }

}
