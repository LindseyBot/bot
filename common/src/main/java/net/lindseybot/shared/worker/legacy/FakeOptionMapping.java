package net.lindseybot.shared.worker.legacy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gnu.trove.map.TLongObjectMap;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Getter
@Setter
public class FakeOptionMapping {

    private String name;
    private String value;
    private OptionType type;

    @JsonIgnore
    private TLongObjectMap<Object> resolved;

}
