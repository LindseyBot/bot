package net.lindseybot.legacy.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.lindseybot.legacy.models.IRedisConfig;
import net.lindseybot.legacy.properties.LegacyProperties;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

@Service
public class LegacyService {

    private final JedisPool jedisPool;

    public LegacyService(LegacyProperties properties) {
        this.jedisPool = new JedisPool(new IRedisConfig(),
                properties.getIp(),
                properties.getPort(), 2000,
                properties.getPassword());
    }

    public boolean isIgnored(Guild guild, Channel channel) {
        try (Jedis redis = this.jedisPool.getResource()) {
            return redis.sismember("LewdBot:Ignored:" + guild.getId(), channel.getId());
        } catch (Exception ex) {
            return false;
        }
    }

    public void setPrefix(Guild guild, String prefix) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            if (prefix == null) {
                jedis.hdel("LewdBot:Profile:" + guild.getId(), "prefix");
            } else {
                jedis.hset("LewdBot:Profile:" + guild.getId(), "prefix", prefix);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to update prefix");
        }
    }

    public String getPrefix(Guild guild) {
        String prefix;
        try (Jedis jedis = this.jedisPool.getResource()) {
            prefix = jedis.hget("LewdBot:Profile:" + guild.getId(), "prefix");
        } catch (Exception ex) {
            return "L!";
        }
        return Objects.requireNonNullElse(prefix, "L!");
    }

}
