package net.lindseybot.economy.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlackjackModel {

    private long id;
    private long count;
    private List<Integer> hand;
    private List<Integer> botHand = new ArrayList<>();

    public int getSum() {
        return hand.stream().mapToInt(Integer::intValue).sum();
    }

}
