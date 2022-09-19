package net.lindseybot.shared.worker.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.trove.map.TLongObjectMap;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.lindseybot.shared.worker.impl.DefaultInteractionListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class LegacyListener implements MessageListener {

    private final ShardManager api;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private DefaultInteractionListener listener = null;

    public LegacyListener(ShardManager api) {
        this.api = api;
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        FakeSlashData data;
        try {
            data = objectMapper.readValue(message.getBody(), FakeSlashData.class);
        } catch (IOException ex) {
            return;
        }
        Guild guild = this.api.getGuildById(data.getGuildId());
        if (guild == null) {
            return;
        }
        this.resolve(guild, data.getOptions());
        // --
        GuildChannel channel = guild.getGuildChannelById(data.getChannelId());
        if (!(channel instanceof GuildMessageChannel msgChannel)) {
            return;
        } else if (!msgChannel.canTalk()) {
            return;
        }
        try {
            var msg = msgChannel.retrieveMessageById(data.getMessageId())
                    .complete();
            if (msg == null) {
                return;
            }
            FakeSlashCommand command = new FakeSlashCommand(this.api, data, msg);
            DefaultInteractionListener listener = this.getListener();
            if (listener == null) {
                log.warn("Received a legacy command but no listener was found.");
                return;
            }
            listener.onSlashCommandInteraction(command);
        } catch (InsufficientPermissionException ex) {
            // Ignored
        }
    }

    private void resolve(Guild guild, Map<String, FakeOptionMapping> options) {
        options.forEach((name, value) -> {
            switch (value.getType()) {
                case USER -> {
                    TLongObjectMap<Object> resolved = MiscUtil.newLongMap();
                    Member member = guild.retrieveMemberById(value.getValue())
                            .complete();
                    if (member != null) {
                        resolved.put(member.getIdLong(), member);
                        value.setResolved(resolved);
                        return;
                    }
                    User user = this.api.retrieveUserById(value.getValue())
                            .complete();
                    if (user != null) {
                        resolved.put(user.getIdLong(), user);
                        value.setResolved(resolved);
                    } else {
                        log.warn("Invalid user: " + value.getValue());
                    }
                }
                case CHANNEL -> {
                    GuildChannel channel = guild.getGuildChannelById(value.getValue());
                    if (channel == null) {
                        log.warn("Invalid channel: " + value.getValue());
                        return;
                    }
                    TLongObjectMap<Object> resolved = MiscUtil.newLongMap();
                    resolved.put(channel.getIdLong(), channel);
                    value.setResolved(resolved);
                }
                case ROLE -> {
                    Role role = guild.getRoleById(value.getValue());
                    if (role == null) {
                        log.warn("Invalid role: " + value.getValue());
                        return;
                    }
                    TLongObjectMap<Object> resolved = MiscUtil.newLongMap();
                    resolved.put(role.getIdLong(), role);
                    value.setResolved(resolved);
                }
            }
        });
    }

    private DefaultInteractionListener getListener() {
        if (this.listener == null) {
            listener = (DefaultInteractionListener) api.getShards().get(0)
                    .getRegisteredListeners()
                    .stream().filter(l -> l.getClass().getName().equals(DefaultInteractionListener.class.getName()))
                    .findAny().orElse(null);
        }
        return this.listener;
    }

}
