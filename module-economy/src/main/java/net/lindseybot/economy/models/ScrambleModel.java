package net.lindseybot.economy.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ScrambleModel {

    @Id
    private long id;
    private long channelId;
    private long startTime;
    private String word;
    private String scrambled;

}
