package net.lindseybot.shared.properties;

import lombok.Data;

@Data
public class BotProperties {

    private String token;
    private ShardProperties shards;
    private String gateway;
    private String rest;
    private Integer intents;

}
