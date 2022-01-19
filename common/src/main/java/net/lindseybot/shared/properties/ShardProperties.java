package net.lindseybot.shared.properties;

import lombok.Data;

import java.util.List;

@Data
public class ShardProperties {

    private Integer total;
    private List<Integer> shards;
    private Integer min;
    private Integer max;

}
