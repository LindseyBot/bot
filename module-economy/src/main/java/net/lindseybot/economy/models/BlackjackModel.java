package net.lindseybot.economy.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Data
@RedisHash(value = "Lindsey:Blackjack", timeToLive = 60)
public class BlackjackModel {

    @Id
    private long id;
    private long count;
    private List<Integer> hand;
    private List<Integer> botHand = new ArrayList<>();

    public int getSum() {
        return hand.stream().mapToInt(Integer::intValue).sum();
    }

}
