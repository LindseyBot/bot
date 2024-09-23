package net.lindseybot.legacy.listeners;

import gnu.trove.map.TLongObjectMap;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.lindseybot.legacy.fake.FakeSlashCommand;
import net.lindseybot.legacy.fake.FakeSlashData;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.legacy.services.LegacyService;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.impl.DefaultInteractionListener;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CommandListener extends ListenerAdapter {

    private final Messenger msg;
    private final LegacyService legacy;
    private final Pattern pattern = Pattern.compile("(?:([^\\s\"]+)|\"((?:\\w+|\\\\\"|[^\"])+)\")");

    private final Map<String, SlashConverter> converters = new HashMap<>();
    private final DefaultInteractionListener listener;

    public CommandListener(
            Messenger msg,
            LegacyService legacy,
            DefaultInteractionListener listener,
            List<SlashConverter> converters,
            IEventManager api) {
        this.msg = msg;
        this.legacy = legacy;
        this.listener = listener;
        converters.forEach((c) -> {
            for (String name : c.getNames()) {
                this.converters.put(name, c);
            }
        });
        api.register(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        } else if (!event.isFromGuild() || event.getMember() == null) {
            return;
        } else if (legacy.isIgnored(event.getGuild(), event.getChannel())) {
            return;
        }
        String rawMessage = event.getMessage().getContentRaw();
        if (rawMessage.split("\\s+").length == 0) {
            return;
        }
        String prefix = this.findPrefix(rawMessage.split("\\s+")[0].toLowerCase(), event.getGuild(),
                event.getGuild().getSelfMember());
        if (prefix == null) {
            return;
        }
        // -- Argument Finder
        List<String> arguments = this.getArguments(event.getMessage().getContentDisplay().substring(prefix.length()));
        if (arguments.isEmpty()) {
            msg.send(event.getGuildChannel(), Label.raw("Hmm?"));
            return;
        }
        String name = arguments.get(0).toLowerCase();
        if (arguments.size() == 1) {
            arguments.clear();
        } else {
            arguments.remove(0);
        }
        // -- Parse all arguments using converter
        SlashConverter converter = this.converters.get(name);
        if (converter == null) {
            return;
        }
        FakeSlashData data = converter.convert(event, name, arguments.toArray(new String[0]));
        if (data == null) {
            return;
        }
        // -- Populate special parameters
        data.getOptions().forEach((optName, option) -> {
            if (option.getType() == OptionType.USER) {
                TLongObjectMap<Object> resolved = MiscUtil.newLongMap();
                Member member = event.getGuild().retrieveMemberById(option.getValue())
                        .complete();
                if (member != null) {
                    resolved.put(member.getIdLong(), member);
                    option.setResolved(resolved);
                    return;
                }
                User user = event.getJDA().retrieveUserById(option.getValue())
                        .complete();
                if (user != null) {
                    resolved.put(user.getIdLong(), user);
                    option.setResolved(resolved);
                } else {
                    log.warn("Invalid user: {}", option.getValue());
                }
            } else if (option.getType() == OptionType.CHANNEL) {
                GuildChannel channel = event.getGuild().getGuildChannelById(option.getValue());
                if (channel == null) {
                    log.warn("Invalid channel: {}", option.getValue());
                    return;
                }
                TLongObjectMap<Object> resolved = MiscUtil.newLongMap();
                resolved.put(channel.getIdLong(), channel);
                option.setResolved(resolved);
            } else if (option.getType() == OptionType.ROLE) {
                Role role = event.getGuild().getRoleById(option.getValue());
                if (role == null) {
                    log.warn("Invalid role: {}", option.getValue());
                    return;
                }
                TLongObjectMap<Object> resolved = MiscUtil.newLongMap();
                resolved.put(role.getIdLong(), role);
                option.setResolved(resolved);
            }
        });
        // -- Fire slash command event
        FakeSlashCommand command = new FakeSlashCommand(
                event.getJDA().getShardManager(), data, event.getMessage());
        this.listener.onSlashCommandInteraction(command);
    }

    private String findPrefix(String message, Guild guild, Member self) {
        if (message.startsWith("l!")) {
            return "l!";
        } else if (message.startsWith(self.getAsMention()) || message.startsWith("<@!119482224713269248>")) {
            return "@" + self.getEffectiveName();
        } else {
            String prefix = this.legacy.getPrefix(guild);
            if (message.startsWith(prefix)) {
                return prefix;
            }
        }
        return null;
    }

    private List<String> getArguments(String rawArgs) {
        List<String> args = new ArrayList<>();
        Matcher m = pattern.matcher(rawArgs);
        while (m.find()) {
            if (m.group(1) == null) {
                args.add(m.group(2));
            } else {
                args.add(m.group(1));
            }
        }
        return args;
    }

}
