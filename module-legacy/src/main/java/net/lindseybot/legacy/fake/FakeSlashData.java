package net.lindseybot.legacy.fake;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FakeSlashData {

    private long responseNumber;
    private long channelId;
    private long guildId;
    private long messageId;
    private long memberId;

    private String path;
    private Map<String, FakeOptionMapping> options = new HashMap<>();

}
