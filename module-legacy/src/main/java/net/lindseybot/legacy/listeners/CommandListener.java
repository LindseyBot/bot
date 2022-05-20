package net.lindseybot.legacy.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.legacy.services.LegacyService;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.legacy.FakeSlashData;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    private final Map<String, SlashConverter> converters;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CommandListener(IEventManager api,
                           Messenger msg,
                           LegacyService legacy,
                           List<SlashConverter> converters,
                           StringRedisTemplate redis) {
        api.register(this);
        this.msg = msg;
        this.legacy = legacy;
        this.redis = redis;
        this.converters = new HashMap<>();
        converters.forEach((c) -> {
            for (String name : c.getNames()) {
                this.converters.put(name, c);
            }
        });
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
        // -- Convert & Send
        SlashConverter converter = this.converters.get(name);
        if (converter == null) {
            return;
        }
        FakeSlashData data = converter.convert(event, name, arguments.toArray(new String[0]));
        if (data == null) {
            return;
        }
        String json;
        try {
            json = objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            return;
        }
        redis.convertAndSend("legacy", json);
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
