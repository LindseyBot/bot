package net.lindseybot.testing.services;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.lindseybot.shared.enums.Language;
import net.lindseybot.shared.enums.LeaderboardType;
import net.lindseybot.shared.worker.services.Translator;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommandMappingService {

    private final Translator i18n;
    private final Map<String, SlashCommandData> commands = new HashMap<>();

    public CommandMappingService(Translator i18n) {
        this.i18n = i18n;
        commands.put("giveme", this.giveme());
        commands.put("bet", this.bet());
        commands.put("blackjack", this.blackjack());
        commands.put("cookies", this.cookies());
        commands.put("leaderboard", this.leaderboard());
        commands.put("profile", this.profile());
        commands.put("scramble", this.scramble());
        commands.put("calc", this.calc());
        commands.put("color", this.color());
        commands.put("flip", this.flip());
        commands.put("roll", this.roll());
        commands.put("lindsey", this.lindsey());
        commands.put("apoiase", this.apoiase());
        commands.put("catarse", this.catarse());
        commands.put("anime", this.anime());
        commands.put("kitsu", this.kitsu());
        commands.put("mal", this.mal());
        commands.put("picarto", this.picarto());
        commands.put("twitch", this.twitch());
        commands.put("hackban", this.hackban());
        commands.put("prune", this.prune());
        commands.put("softban", this.softban());
        commands.put("voice", this.voice());
        commands.put("nsfw", this.nsfw());
        commands.put("define", this.define());
        commands.put("hearthstone", this.hearthstone());
        commands.put("pokedex", this.pokedex());

        commands.put("dev", this.dev());
    }

    public SlashCommandData getByName(String name) {
        return this.commands.get(name);
    }

    public Collection<SlashCommandData> getCommands() {
        return this.commands.values();
    }

    private String i18n(String label) {
        return this.i18n.get(Language.en_US, label);
    }

    private SlashCommandData giveme() {
        SlashCommandData command = Commands.slash("giveme", i18n("commands.giveme.description"));
        SubcommandData list = new SubcommandData("list", i18n("commands.giveme.list"));
        command.addSubcommands(list);

        SubcommandData get = new SubcommandData("get", i18n("commands.giveme.get"));
        OptionData option = new OptionData(OptionType.STRING, "name", i18n("commands.giveme.role"), true);
        option.setAutoComplete(true);
        get.addOptions(option);
        command.addSubcommands(get);

        SubcommandData add = new SubcommandData("add", i18n("commands.giveme.add"));
        add.addOption(OptionType.ROLE, "name", i18n("commands.giveme.role"), true);
        command.addSubcommands(add);
        SubcommandData remove = new SubcommandData("remove", i18n("commands.giveme.remove"));
        remove.addOption(OptionType.ROLE, "name", i18n("commands.giveme.role"), true);
        command.addSubcommands(remove);
        return command;
    }

    private SlashCommandData bet() {
        return Commands.slash("bet", i18n("commands.bet.description"));
    }

    private SlashCommandData blackjack() {
        return Commands.slash("blackjack", i18n("commands.blackjack"));
    }

    private SlashCommandData cookies() {
        SlashCommandData command = Commands.slash("cookies", i18n("commands.cookies.description"));
        SubcommandData daily = new SubcommandData("daily", i18n("commands.cookies.daily.description"));
        command.addSubcommands(daily);
        SubcommandData send = new SubcommandData("send", i18n("commands.cookies.send.description"));
        send.addOption(OptionType.USER, "target", i18n("commands.cookies.send.target"), true);
        send.addOption(OptionType.INTEGER, "amount", i18n("commands.cookies.send.amount"), true);
        command.addSubcommands(send);
        SubcommandData balance = new SubcommandData("balance", i18n("commands.cookies.balance.description"));
        balance.addOption(OptionType.USER, "user", i18n("commands.cookies.balance.user"), false);
        command.addSubcommands(balance);
        return command;
    }

    private SlashCommandData leaderboard() {
        SlashCommandData command = Commands.slash("leaderboard", i18n("commands.leaderboard.description"));
        OptionData data = new OptionData(OptionType.STRING, "name", i18n("commands.leaderboard.type"), true);
        for (LeaderboardType value : LeaderboardType.values()) {
            data.addChoice(value.getPrettyName(), value.name());
        }
        command.addOptions(data);
        return command;
    }

    private SlashCommandData profile() {
        SlashCommandData data = Commands.slash("profile", i18n("commands.profile.description"));
        data.addOption(OptionType.USER, "target", i18n("commands.profile.target"), false);
        return data;
    }

    private SlashCommandData scramble() {
        return Commands.slash("scramble", i18n("commands.scramble.description"));
    }

    private SlashCommandData calc() {
        SlashCommandData command = Commands.slash("calc", i18n("commands.calc.description"));
        command.addOption(OptionType.STRING, "expression", i18n("commands.calc.expression"), true);
        return command;
    }

    private SlashCommandData color() {
        SlashCommandData command = Commands.slash("color", i18n("commands.color.description"));
        command.addOption(OptionType.STRING, "hex", i18n("commands.color.hex"), true);
        return command;
    }

    private SlashCommandData flip() {
        return Commands.slash("flip", i18n("commands.flip.description"));
    }

    private SlashCommandData roll() {
        SlashCommandData command = Commands.slash("roll", i18n("commands.roll.description"));
        command.addOption(OptionType.INTEGER, "sides", i18n("commands.roll.sides"), false);
        return command;
    }

    private SlashCommandData lindsey() {
        SlashCommandData data = Commands.slash("lindsey", i18n("commands.lindsey.description"));

        SubcommandGroupData modules = new SubcommandGroupData("modules", i18n("commands.lindsey.modules"));

        SubcommandData enable = new SubcommandData("enable", i18n("commands.lindsey.modules.enable"));
        enable.addOption(OptionType.STRING, "name", i18n("commands.lindsey.modules.enable.name"), true);
        modules.addSubcommands(enable);

        SubcommandData disable = new SubcommandData("disable", i18n("commands.lindsey.modules.disable"));
        disable.addOption(OptionType.STRING, "name", i18n("commands.lindsey.modules.disable.name"), true);
        modules.addSubcommands(disable);

        SubcommandData list = new SubcommandData("list", i18n("commands.lindsey.modules.list"));
        modules.addSubcommands(list);

        SubcommandData status = new SubcommandData("status", i18n("commands.lindsey.modules.status"));
        status.addOption(OptionType.STRING, "name", i18n("commands.lindsey.modules.status.name"), true);
        modules.addSubcommands(status);

        SubcommandData configure = new SubcommandData("configure", i18n("commands.lindsey.modules.configure"));
        configure.addOption(OptionType.STRING, "name", i18n("commands.lindsey.modules.configure.name"), true);
        modules.addSubcommands(configure);

        SubcommandData logs = new SubcommandData("logs", i18n("commands.lindsey.modules.logs"));
        modules.addSubcommands(logs);

        data.addSubcommandGroups(modules);
        return data;
    }

    private SlashCommandData apoiase() {
        SlashCommandData command = Commands.slash("apoiase", i18n("commands.apoiase.description"));
        command.addOption(OptionType.STRING, "search", i18n("commands.crowdfunding.search"), true);
        return command;
    }

    private SlashCommandData catarse() {
        SlashCommandData command = Commands.slash("catarse", i18n("commands.catarse.description"));

        SubcommandData user = new SubcommandData("user", i18n("commands.catarse.user"));
        user.addOption(OptionType.STRING, "name", i18n("commands.crowdfunding.search"), true);
        command.addSubcommands(user);

        SubcommandData project = new SubcommandData("project", i18n("commands.catarse.project"));
        project.addOption(OptionType.STRING, "name", i18n("commands.crowdfunding.search"), true);
        command.addSubcommands(project);

        return command;
    }

    private SlashCommandData anime() {
        SlashCommandData command = Commands.slash("anime", i18n("commands.kitsu.description"));
        command.addOption(OptionType.STRING, "name", i18n("commands.kitsu.name"), true);
        return command;
    }

    private SlashCommandData kitsu() {
        SlashCommandData command = Commands.slash("kitsu", i18n("commands.kitsu.description"));
        command.addOption(OptionType.STRING, "name", i18n("commands.kitsu.name"), true);
        return command;
    }

    private SlashCommandData mal() {
        SlashCommandData command = Commands.slash("mal", i18n("commands.kitsu.description"));
        command.addOption(OptionType.STRING, "name", i18n("commands.kitsu.name"), true);
        return command;
    }

    private SlashCommandData picarto() {
        SlashCommandData command = Commands.slash("picarto", i18n("commands.picarto.description"));
        command.addOption(OptionType.STRING, "name", i18n("commands.lives.search"), true);
        return command;
    }

    private SlashCommandData twitch() {
        SlashCommandData command = Commands.slash("twitch", i18n("commands.twitch.description"));
        command.addOption(OptionType.STRING, "name", i18n("commands.lives.search"), true);
        return command;
    }

    private SlashCommandData hackban() {
        SlashCommandData data = Commands.slash("hackban", i18n("commands.hackban.description"));
        data.addOption(OptionType.USER, "user", i18n("commands.hackban.user"), true);
        data.addOption(OptionType.STRING, "reason", i18n("commands.hackban.reason"), true);
        return data;
    }

    private SlashCommandData prune() {
        SlashCommandData data = Commands.slash("prune", i18n("commands.prune.description"));
        data.addOption(OptionType.INTEGER, "count", i18n("commands.prune.count"), true);
        data.addOption(OptionType.USER, "user", i18n("commands.prune.user"), false);
        return data;
    }

    private SlashCommandData softban() {
        SlashCommandData data = Commands.slash("softban", i18n("commands.softban.description"));
        data.addOption(OptionType.USER, "user", i18n("commands.softban.user"), true);
        data.addOption(OptionType.STRING, "reason", i18n("commands.softban.reason"), false);
        return data;
    }

    private SlashCommandData voice() {
        SubcommandData split = new SubcommandData("split", i18n("commands.voice.split.description"));
        split.addOption(OptionType.CHANNEL, "from", i18n("commands.voice.split.from"), true);
        split.addOption(OptionType.CHANNEL, "to", i18n("commands.voice.split.to"), true);

        SubcommandData move = new SubcommandData("move", i18n("commands.voice.move.description"));
        move.addOption(OptionType.CHANNEL, "from", i18n("commands.voice.move.from"), true);
        move.addOption(OptionType.CHANNEL, "to", i18n("commands.voice.move.to"), true);

        SlashCommandData data = Commands.slash("voice", i18n("commands.voice.description"));
        data.addSubcommands(split);
        data.addSubcommands(move);
        return data;
    }

    private SlashCommandData nsfw() {
        SlashCommandData data = Commands.slash("nsfw", i18n("commands.nsfw.description"));
        OptionData option = new OptionData(OptionType.STRING, "tags", i18n("commands.nsfw.filter"), false);

        SubcommandData rule34 = new SubcommandData("rule34", i18n("commands.nsfw.rule34"));
        rule34.addOptions(option);
        data.addSubcommands(rule34);

        SubcommandData danbooru = new SubcommandData("danbooru", i18n("commands.nsfw.danbooru"));
        danbooru.addOptions(option);
        data.addSubcommands(danbooru);

        SubcommandData furry = new SubcommandData("furry", i18n("commands.nsfw.e621"));
        furry.addOptions(option);
        data.addSubcommands(furry);

        SubcommandData gelbooru = new SubcommandData("gelbooru", i18n("commands.nsfw.gelbooru"));
        gelbooru.addOptions(option);
        data.addSubcommands(gelbooru);

        return data;
    }

    private SlashCommandData define() {
        SlashCommandData command = Commands.slash("define", i18n("commands.define.description"));
        command.addOption(OptionType.STRING, "word", i18n("commands.define.word"), true);
        return command;
    }

    private SlashCommandData hearthstone() {
        SlashCommandData command = Commands.slash("hearthstone", i18n("commands.hearthstone.description"));
        command.addOption(OptionType.STRING, "card", i18n("commands.hearthstone.card"), true);
        command.addOption(OptionType.BOOLEAN, "gold", i18n("commands.hearthstone.gold"), false);
        return command;
    }

    private SlashCommandData pokedex() {
        SlashCommandData command = Commands.slash("pokedex", i18n("commands.pokedex.description"));
        command.addOption(OptionType.STRING, "search", i18n("commands.pokedex.search"), true);
        return command;
    }

    // ------------------------------

    private SlashCommandData dev() {
        SubcommandGroupData commands = new SubcommandGroupData("commands", "Slash command management");
        {
            SubcommandData publish = new SubcommandData("publish", "Publish slash commands.");
            publish.addOption(OptionType.STRING, "name", "Command name", true);
            publish.addOption(OptionType.BOOLEAN, "global", "Is Global Publish", false);
            commands.addSubcommands(publish);
            SubcommandData remove = new SubcommandData("remove", "Remove slash commands.");
            remove.addOption(OptionType.STRING, "name", "Command name", true);
            remove.addOption(OptionType.BOOLEAN, "global", "Remove globally", false);
            commands.addSubcommands(remove);
            SubcommandData list = new SubcommandData("list", "List all registered commands.");
            OptionData listFilter = new OptionData(OptionType.STRING, "filter", "List filter", true);
            listFilter.addChoice("Known (Internal)", "known");
            listFilter.addChoice("Published (Guild)", "guild");
            listFilter.addChoice("Published (Global)", "global");
            list.addOptions(listFilter);
            commands.addSubcommands(list);
        }
        SubcommandGroupData items = new SubcommandGroupData("items", "Item management");
        {
            SubcommandData publishItem = new SubcommandData("publish", "Publish an item");
            publishItem.addOption(OptionType.STRING, "name", "Item name", true);
            items.addSubcommands(publishItem);
        }
        SlashCommandData data = Commands.slash("dev", "Developer commands.");
        data.addSubcommandGroups(commands);
        data.addSubcommandGroups(items);
        return data;
    }

}
