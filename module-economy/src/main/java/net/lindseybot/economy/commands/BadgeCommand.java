package net.lindseybot.economy.commands;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.lindseybot.economy.properties.ImageGenProperties;
import net.lindseybot.economy.repositories.sql.BadgeRepository;
import net.lindseybot.economy.services.CustomizationService;
import net.lindseybot.economy.services.InventoryService;
import net.lindseybot.shared.entities.discord.*;
import net.lindseybot.shared.entities.discord.builders.ButtonBuilder;
import net.lindseybot.shared.entities.discord.builders.MessageBuilder;
import net.lindseybot.shared.entities.discord.builders.SelectMenuBuilder;
import net.lindseybot.shared.entities.discord.builders.SelectOptionBuilder;
import net.lindseybot.shared.entities.items.Badge;
import net.lindseybot.shared.entities.items.UserItem;
import net.lindseybot.shared.entities.profile.users.Customization;
import net.lindseybot.shared.worker.InteractionHandler;
import net.lindseybot.shared.worker.SelectMenu;
import net.lindseybot.shared.worker.SlashCommand;
import net.lindseybot.shared.worker.services.Messenger;
import net.lindseybot.shared.worker.services.Translator;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BadgeCommand extends InteractionHandler {

    private final Translator i18n;
    private final String endpoint;
    private final BadgeRepository badges;
    private final InventoryService inventory;
    private final CustomizationService customization;
    private final OkHttpClient client = new OkHttpClient();

    public BadgeCommand(Messenger msg,
                        Translator i18n, ImageGenProperties properties,
                        BadgeRepository badges,
                        InventoryService inventory, CustomizationService customization) {
        super(msg);
        this.i18n = i18n;
        this.endpoint = "http://" + properties.getIp() + ":" + properties.getPort() + "/badges";
        this.badges = badges;
        this.inventory = inventory;
        this.customization = customization;
    }

    @SlashCommand("badge.list")
    public void onList(SlashCommandInteractionEvent event) {
        List<Badge> badges = this.badges.findAll();
        Badge badge = badges.get(0);
        FMessage message = new MessageBuilder()
                .content(Label.raw("\n"))
                .addComponent(this.getSelectMenu(badge))
                .addComponent(new ButtonBuilder().secondary("badge.next:1", Label.raw("Next")).build())
                .attach(this.getInfo(event.getUser(), badge,
                        this.inventory.hasItem(event.getUser().getIdLong(), badge.getId())))
                .build();
        this.msg.reply(event, message);
    }

    @SelectMenu("badge.select")
    public void onListSelect(SelectMenuInteractionEvent event) {
        if (event.getSelectedOptions().isEmpty()) {
            return;
        }
        String selected = this.getSelected(event);
        if (selected == null) {
            return;
        }
        List<Badge> badges = this.badges.findAll();
        Badge badge = badges.stream()
                .filter(b -> b.getId().equals(Long.parseLong(selected)))
                .findFirst().orElse(null);
        if (badge == null) {
            return;
        }
        FMessage message = new MessageBuilder()
                .content(Label.raw("\n"))
                .addComponent(this.getSelectMenu(badge))
                .addComponent(new ButtonBuilder().secondary("badge.next:1", Label.raw("Next")).build())
                .attach(this.getInfo(event.getUser(), badge,
                        this.inventory.hasItem(event.getUser().getIdLong(), badge.getId())))
                .build();
        this.msg.reply(event, message);
    }

    @SlashCommand("badge.equip")
    public void onEquip(SlashCommandInteractionEvent event) {
        List<Long> items = this.inventory.findBadges(event.getUser().getIdLong())
                .stream().map(UserItem::getItemId)
                .toList();
        if (items.isEmpty()) {
            return;
        }
        Customization customization = this.customization.getCustomization(event.getUser().getIdLong());
        List<Long> equipped = customization.getBadges();
        SelectMenuBuilder menu = new SelectMenuBuilder("badge.equip")
                .withData(event.getUser().getId())
                .withRange(0, 8);
        for (Badge badge : this.badges.findAllById(items)) {
            FSelectOption option = new SelectOptionBuilder(String.valueOf(badge.getId()), Label.raw(badge.getName()))
                    .withDescription(Label.of(badge.getDescription()))
                    .asDefault(equipped.contains(badge.getId()))
                    .build();
            menu.addOption(option);
        }
        FMessage message = new MessageBuilder()
                .content(Label.raw("Please select which badges you want to display below."))
                .addComponent(menu.build())
                .attach(this.getList(equipped))
                .build();
        this.msg.reply(event, message);
    }

    @SelectMenu("badge.equip")
    public void onEquip(SelectMenuInteractionEvent event) {
        String data = this.getData(event);
        if (data == null) {
            return;
        }
        long userId = Long.parseLong(data);
        if (userId != event.getUser().getIdLong()) {
            this.msg.error(event, Label.of("error.interaction"));
            return;
        }
        // -- Save
        Customization customization =
                this.customization.getCustomization(event.getUser().getIdLong());
        if (event.getSelectedOptions().isEmpty()) {
            customization.setBadges(new ArrayList<>());
        } else {
            List<Long> selected = event.getSelectedOptions().stream()
                    .map(SelectOption::getValue)
                    .map(Long::parseLong)
                    .toList();
            customization.setBadges(selected);
        }
        this.customization.save(customization);
        // -- Update message
        List<Long> items = this.inventory.findBadges(event.getUser().getIdLong())
                .stream().map(UserItem::getItemId)
                .toList();
        if (items.isEmpty()) {
            return;
        }
        List<Long> equipped = customization.getBadges();
        SelectMenuBuilder menu = new SelectMenuBuilder("badge.equip")
                .withData(data)
                .withRange(0, 8);
        for (Badge badge : this.badges.findAllById(items)) {
            FSelectOption option = new SelectOptionBuilder(String.valueOf(badge.getId()), Label.raw(badge.getName()))
                    .withDescription(Label.of(badge.getDescription()))
                    .asDefault(equipped.contains(badge.getId()))
                    .build();
            menu.addOption(option);
        }
        FMessage message = new MessageBuilder()
                .content(Label.raw("Please select which badges you want to display below."))
                .addComponent(menu.build())
                .attach(this.getList(equipped))
                .build();
        this.msg.edit(event, message);
    }

    @SlashCommand("badge.info")
    public void onInfo(SlashCommandInteractionEvent event) {
        List<UserItem> items = inventory.findBadges(event.getUser().getIdLong());
    }

    public FSelectMenu getSelectMenu(Badge selected) {
        SelectMenuBuilder menu = new SelectMenuBuilder("badge.picker");
        for (Badge badge : this.badges.findAll()) {
            FSelectOption option = new SelectOptionBuilder(String.valueOf(badge.getId()), Label.raw(badge.getName()))
                    .withDescription(Label.of(badge.getDescription()))
                    .asDefault(selected != null && selected.equals(badge))
                    .build();
            menu.addOption(option);
        }
        return menu.build();
    }

    public FAttachment getInfo(User user, Badge badge, boolean hasItem) {
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
            return new FAttachment("badge.png", body.bytes());
        } catch (IOException ex) {
            log.error("Failed to request badge info", ex);
            return null;
        }
    }

    public FAttachment getList(List<Long> badges) {
        JSONArray array = new JSONArray();
        for (Long id : badges) {
            array.put(id);
        }
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), array.toString());
        Request request = new Request.Builder()
                .url(this.endpoint + "/list")
                .post(reqBody)
                .build();
        Call call = client.newCall(request);
        try (Response response = call.execute(); ResponseBody body = response.body()) {
            if (!response.isSuccessful() || body == null) {
                return null;
            }
            return new FAttachment("equipped.png", body.bytes());
        } catch (IOException ex) {
            log.error("Failed to request badge list", ex);
            return null;
        }
    }

}
