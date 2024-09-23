package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.legacy.fake.FakeOptionMapping;
import net.lindseybot.legacy.fake.FakeSlashData;
import org.springframework.stereotype.Component;

@Component
public class E621Converter extends SlashConverter {

    @Override
    public String[] getNames() {
        return new String[]{"e621", "furry"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        FakeSlashData data = this.createData(event, "nsfw/furry");
        if (args.length > 0) {
            FakeOptionMapping option = new FakeOptionMapping();
            option.setName("tags");
            option.setType(OptionType.STRING);
            option.setValue(argsToString(args, 0));
            data.getOptions().put(option.getName(), option);
        }
        return data;
    }

}
