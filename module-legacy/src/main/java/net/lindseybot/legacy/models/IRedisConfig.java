package net.lindseybot.legacy.models;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;

import java.time.Duration;

public class IRedisConfig extends GenericObjectPoolConfig<Jedis> {

    public IRedisConfig() {
        // defaults to make your life with connection pool easier :)
        setTestWhileIdle(true);
        setMinEvictableIdleTime(Duration.ofMillis(60000));
        setTimeBetweenEvictionRuns(Duration.ofMillis(30000));
        setNumTestsPerEvictionRun(-1);
        setMaxWait(Duration.ofSeconds(30));
        setMaxTotal(1000);
    }

}
