package net.lindseybot.economy.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "Lindsey:Bet", timeToLive = 60)
public class BetModel {

    @Id
    private long id;
    private long count;

}
