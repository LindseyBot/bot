package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.legacy.fake.FakeOptionMapping;
import net.lindseybot.legacy.fake.FakeSlashData;
import org.springframework.stereotype.Component;

@Component
public class ColorConverter extends SlashConverter {

    @Override
    public String[] getNames() {
        return new String[]{"color"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        if (args.length == 0) {
            // TODO: HELP
            return null;
        }
        FakeSlashData data = this.createData(event, "color");
        FakeOptionMapping optColor = new FakeOptionMapping();
        optColor.setName("hex");
        optColor.setType(OptionType.STRING);
        optColor.setValue(argsToString(args, 0));
        data.getOptions().put(optColor.getName(), optColor);
        return data;
    }

}
