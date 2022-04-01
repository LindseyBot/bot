package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.shared.worker.legacy.FakeOptionMapping;
import net.lindseybot.shared.worker.legacy.FakeSlashData;
import org.springframework.stereotype.Component;

@Component
public class GelbooruConverter extends SlashConverter {

    @Override
    public String[] getNames() {
        return new String[]{"gelbooru"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        FakeSlashData data = this.createData(event, "nsfw/gelbooru");
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
