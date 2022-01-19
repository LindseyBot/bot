package net.lindseybot.economy.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.lindseybot.economy.models.ScrambleModel;
import net.lindseybot.economy.services.EconomyService;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ScrambleListener extends ListenerAdapter implements ExpirationListener<Long, ScrambleModel> {

    private final ExpiringMap<Long, ScrambleModel> guilds = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(1, TimeUnit.MINUTES)
            .expirationListener(this)
            .build();

    private final ShardManager jda;
    private final Messenger msg;
    private final EconomyService service;

    public ScrambleListener(Messenger msg, EconomyService service, ShardManager jda) {
        this.msg = msg;
        this.service = service;
        this.jda = jda;
        jda.addEventListener(this);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getMember() == null
                || event.getAuthor().isBot()
                || event.getMember().isPending()
                || !this.has(event.getGuild())) {
            return;
        }
        ScrambleModel data = this.guilds.get(event.getGuild().getIdLong());
        if (data == null) {
            return;
        } else if (event.getChannel().getIdLong() != data.getChannelId()) {
            return;
        } else if (!event.getMessage().getContentDisplay().equalsIgnoreCase(data.getWord())) {
            return;
        }

        Long timeDiff = System.currentTimeMillis() - data.getStartTime();
        int cookies = calculateCookies(data.getWord(), timeDiff);
        this.service.pay(event.getAuthor(), cookies);

        this.msg.send(event.getChannel(),
                Label.of("commands.scramble.correct", event.getMember().getAsMention(), cookies));
        this.guilds.remove(event.getGuild().getIdLong());
    }

    @Override
    public void expired(Long guildId, ScrambleModel data) {
        Guild guild = this.jda.getGuildById(guildId);
        if (guild == null) {
            return;
        }
        TextChannel channel = guild.getTextChannelById(data.getChannelId());
        if (channel == null) {
            return;
        }
        // Timed out
        this.msg.send(channel, Label.of("commands.scramble.timed", data.getWord()));
    }

    private Integer calculateCookies(String word, Long timeDifference) {
        double wordLen = word.length() * 1.6;
        double timeCookies = 60 * (1 - ((timeDifference.doubleValue() / 1000) / 60));
        return 40 + ((int) timeCookies + (int) wordLen);
    }

    public void createGame(ScrambleModel data) {
        this.guilds.put(data.getId(), data);
    }

    public boolean has(Guild guild) {
        return this.guilds.containsKey(guild.getIdLong());
    }

    public ScrambleModel get(Guild guild) {
        return this.guilds.get(guild.getIdLong());
    }

}
