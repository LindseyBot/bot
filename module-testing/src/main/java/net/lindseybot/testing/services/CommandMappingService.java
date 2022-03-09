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
    private final Map<String, CommandData> commands = new HashMap<>();

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
        commands.put("help", this.help());
        commands.put("inventory", this.inventory());
        commands.put("store", this.store());

        commands.put("dev", this.dev());
        // Message context
        commands.put("msg-prune", this.msgPrune());
    }

    public CommandData getByName(String name) {
        return this.commands.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Collection<CommandData> getCommands() {
        return this.commands.values();
    }

    private String i18n(String label) {
        return this.i18n.get(Language.en_US, label);
    }

    // -- Message context

    private CommandData msgPrune() {
        return Commands.message("Delete until here");
    }

    // --

    private SlashCommandData giveme() {
        return Commands.slash("giveme", i18n("commands.giveme.description"))
                .addSubcommands(new SubcommandData("list", i18n("commands.giveme.list")))
                .addSubcommands(new SubcommandData("get", i18n("commands.giveme.get"))
                        .addOptions(new OptionData(OptionType.STRING, "name", i18n("commands.giveme.role"), true)
                                .setAutoComplete(true)))
                .addSubcommands(new SubcommandData("add", i18n("commands.giveme.add"))
                        .addOption(OptionType.ROLE, "name", i18n("commands.giveme.role"), true))
                .addSubcommands(new SubcommandData("remove", i18n("commands.giveme.remove"))
                        .addOption(OptionType.ROLE, "name", i18n("commands.giveme.role"), true));
    }

    private SlashCommandData bet() {
        return Commands.slash("bet", i18n("commands.bet.description"));
    }

    private SlashCommandData blackjack() {
        return Commands.slash("blackjack", i18n("commands.blackjack"));
    }

    private SlashCommandData cookies() {
        return Commands.slash("cookies", i18n("commands.cookies.description"))
                .addSubcommands(new SubcommandData("daily", i18n("commands.cookies.daily.description")))
                .addSubcommands(new SubcommandData("send", i18n("commands.cookies.send.description"))
                        .addOption(OptionType.USER, "target", i18n("commands.cookies.send.target"), true)
                        .addOption(OptionType.INTEGER, "amount", i18n("commands.cookies.send.amount"), true))
                .addSubcommands(new SubcommandData("balance", i18n("commands.cookies.balance.description"))
                        .addOption(OptionType.USER, "user", i18n("commands.cookies.balance.user"), false));
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
        return Commands.slash("profile", i18n("commands.profile.description"))
                .addOption(OptionType.USER, "target", i18n("commands.profile.target"), false);
    }

    private SlashCommandData scramble() {
        return Commands.slash("scramble", i18n("commands.scramble.description"));
    }

    private SlashCommandData calc() {
        return Commands.slash("calc", i18n("commands.calc.description"))
                .addOption(OptionType.STRING, "expression", i18n("commands.calc.expression"), true);
    }

    private SlashCommandData color() {
        return Commands.slash("color", i18n("commands.color.description"))
                .addOption(OptionType.STRING, "hex", i18n("commands.color.hex"), true);
    }

    private SlashCommandData flip() {
        return Commands.slash("flip", i18n("commands.flip.description"));
    }

    private SlashCommandData roll() {
        return Commands.slash("roll", i18n("commands.roll.description"))
                .addOption(OptionType.INTEGER, "sides", i18n("commands.roll.sides"), false);
    }

    private SlashCommandData lindsey() {
        OptionData modules = new OptionData(OptionType.STRING, "name", i18n("commands.lindsey.modules.name"), true);
        modules.addChoice("Anti-Advertising", "antiad");
        modules.addChoice("AntiScam", "antiscam");
        modules.addChoice("KeepRoles", "keeproles");
        modules.addChoice("registration", "Registration");
        modules.addChoice("Starboard", "Starboard");
        modules.addChoice("Welcomer", "welcome");
        return Commands.slash("lindsey", i18n("commands.lindsey.description"))
                .addSubcommandGroups(new SubcommandGroupData("modules", i18n("commands.lindsey.modules"))
                        .addSubcommands(new SubcommandData("list", i18n("commands.lindsey.modules.list")))
                        .addSubcommands(new SubcommandData("enable", i18n("commands.lindsey.modules.enable"))
                                .addOptions(modules))
                        .addSubcommands(new SubcommandData("disable", i18n("commands.lindsey.modules.disable"))
                                .addOptions(modules))
                        .addSubcommands(new SubcommandData("status", i18n("commands.lindsey.modules.status"))
                                .addOptions(modules))
                        .addSubcommands(new SubcommandData("configure", i18n("commands.lindsey.modules.configure"))
                                .addOptions(modules))
                        .addSubcommands(new SubcommandData("logs", i18n("commands.lindsey.modules.logs"))));
    }

    private SlashCommandData apoiase() {
        return Commands.slash("apoiase", i18n("commands.apoiase.description"))
                .addOption(OptionType.STRING, "search", i18n("commands.crowdfunding.search"), true);
    }

    private SlashCommandData catarse() {
        return Commands.slash("catarse", i18n("commands.catarse.description"))
                .addSubcommands(new SubcommandData("user", i18n("commands.catarse.user"))
                        .addOption(OptionType.STRING, "name", i18n("commands.crowdfunding.search"), true))
                .addSubcommands(new SubcommandData("project", i18n("commands.catarse.project"))
                        .addOption(OptionType.STRING, "name", i18n("commands.crowdfunding.search"), true));
    }

    private SlashCommandData anime() {
        return Commands.slash("anime", i18n("commands.kitsu.description"))
                .addOption(OptionType.STRING, "name", i18n("commands.kitsu.name"), true);
    }

    private SlashCommandData kitsu() {
        return Commands.slash("kitsu", i18n("commands.kitsu.description"))
                .addOption(OptionType.STRING, "name", i18n("commands.kitsu.name"), true);
    }

    private SlashCommandData mal() {
        return Commands.slash("mal", i18n("commands.kitsu.description"))
                .addOption(OptionType.STRING, "name", i18n("commands.kitsu.name"), true);
    }

    private SlashCommandData picarto() {
        return Commands.slash("picarto", i18n("commands.picarto.description"))
                .addOption(OptionType.STRING, "name", i18n("commands.lives.search"), true);
    }

    private SlashCommandData twitch() {
        return Commands.slash("twitch", i18n("commands.twitch.description"))
                .addOption(OptionType.STRING, "name", i18n("commands.lives.search"), true);
    }

    private SlashCommandData hackban() {
        return Commands.slash("hackban", i18n("commands.hackban.description"))
                .addOption(OptionType.USER, "user", i18n("commands.hackban.user"), true)
                .addOption(OptionType.STRING, "reason", i18n("commands.hackban.reason"), true);
    }

    private SlashCommandData prune() {
        return Commands.slash("prune", i18n("commands.prune.description"))
                .addOption(OptionType.INTEGER, "count", i18n("commands.prune.count"), true)
                .addOption(OptionType.USER, "user", i18n("commands.prune.user"), false);
    }

    private SlashCommandData softban() {
        return Commands.slash("softban", i18n("commands.softban.description"))
                .addOption(OptionType.USER, "user", i18n("commands.softban.user"), true)
                .addOption(OptionType.STRING, "reason", i18n("commands.softban.reason"), false);
    }

    private SlashCommandData voice() {
        return Commands.slash("voice", i18n("commands.voice.description"))
                .addSubcommands(new SubcommandData("split", i18n("commands.voice.split.description"))
                        .addOption(OptionType.CHANNEL, "from", i18n("commands.voice.split.from"), true)
                        .addOption(OptionType.CHANNEL, "to", i18n("commands.voice.split.to"), true))
                .addSubcommands(new SubcommandData("move", i18n("commands.voice.move.description"))
                        .addOption(OptionType.CHANNEL, "from", i18n("commands.voice.move.from"), true)
                        .addOption(OptionType.CHANNEL, "to", i18n("commands.voice.move.to"), true));
    }

    private SlashCommandData nsfw() {
        OptionData option = new OptionData(OptionType.STRING, "tags", i18n("commands.nsfw.filter"), false);
        return Commands.slash("nsfw", i18n("commands.nsfw.description"))
                .addSubcommands(new SubcommandData("rule34", i18n("commands.nsfw.rule34"))
                        .addOptions(option))
                .addSubcommands(new SubcommandData("danbooru", i18n("commands.nsfw.danbooru"))
                        .addOptions(option))
                .addSubcommands(new SubcommandData("furry", i18n("commands.nsfw.e621"))
                        .addOptions(option))
                .addSubcommands(new SubcommandData("gelbooru", i18n("commands.nsfw.gelbooru"))
                        .addOptions(option));
    }

    private SlashCommandData define() {
        return Commands.slash("define", i18n("commands.define.description"))
                .addOption(OptionType.STRING, "word", i18n("commands.define.word"), true);
    }

    private SlashCommandData hearthstone() {
        return Commands.slash("hearthstone", i18n("commands.hearthstone.description"))
                .addOption(OptionType.STRING, "card", i18n("commands.hearthstone.card"), true)
                .addOption(OptionType.BOOLEAN, "gold", i18n("commands.hearthstone.gold"), false);
    }

    private SlashCommandData pokedex() {
        return Commands.slash("pokedex", i18n("commands.pokedex.description"))
                .addOption(OptionType.STRING, "search", i18n("commands.pokedex.search"), true);
    }

    private SlashCommandData help() {
        return Commands.slash("help", i18n("commands.help.description"))
                .addOptions(new OptionData(OptionType.STRING, "topic", i18n("commands.help.topic"), false)
                        .addChoice("Automod", "automod")
                        .addChoice("Economy", "economy")
                        .addChoice("Moderation", "moderation"));
    }

    private SlashCommandData store() {
        return Commands.slash("store", i18n("commands.store"))
                //.addSubcommands(new SubcommandData("info", i18n("commands.store.info"))
                .addSubcommandGroups(new SubcommandGroupData("list", i18n("commands.store.list"))
                        .addSubcommands(new SubcommandData("badges", i18n("commands.store.list.badges"))));
    }

    private SlashCommandData inventory() {
        return Commands.slash("inventory", i18n("commands.inventory"))
                .addSubcommandGroups(new SubcommandGroupData("equip", i18n("commands.inventory.equip"))
                        .addSubcommands(new SubcommandData("badges", i18n("commands.inventory.equip.badges"))));
    }

    // ------------------------------

    private SlashCommandData dev() {
        return Commands.slash("dev", "Developer commands.")
                .addSubcommandGroups(new SubcommandGroupData("commands", "Slash command management")
                        .addSubcommands(new SubcommandData("publish", "Publish slash commands.")
                                .addOptions(new OptionData(OptionType.STRING, "name", "Command name", true)
                                        .setAutoComplete(true))
                                .addOption(OptionType.BOOLEAN, "global", "Is Global Publish", false))
                        .addSubcommands(new SubcommandData("remove", "Remove slash commands.")
                                .addOptions(new OptionData(OptionType.STRING, "name", "Command name", true)
                                        .setAutoComplete(true))
                                .addOption(OptionType.BOOLEAN, "global", "Remove globally", false))
                        .addSubcommands(new SubcommandData("list", "List all registered commands.")
                                .addOptions(new OptionData(OptionType.STRING, "filter", "List filter", true)
                                        .addChoice("Known (Internal)", "known")
                                        .addChoice("Published (Guild)", "guild")
                                        .addChoice("Published (Global)", "global"))))
                .addSubcommandGroups(new SubcommandGroupData("items", "Item management")
                        .addSubcommands(new SubcommandData("publish", "Publish an item")
                                .addOption(OptionType.STRING, "name", "Item name", true)));
    }

}
