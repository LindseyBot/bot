package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.legacy.fake.FakeOptionMapping;
import net.lindseybot.legacy.fake.FakeSlashData;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class RollConverter extends SlashConverter {

    private final Messenger msg;

    public RollConverter(Messenger msg) {
        this.msg = msg;
    }

    @Override
    public String[] getNames() {
        return new String[]{"roll"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        FakeSlashData data = this.createData(event, "roll");
        if (args.length > 0) {
            int count;
            try {
                count = Integer.parseInt(args[0]);
            } catch (IllegalArgumentException ex) {
                this.msg.reply(event, Label.raw("That's not a number"));
                return null;
            }
            FakeOptionMapping option = new FakeOptionMapping();
            option.setName("sides");
            option.setType(OptionType.INTEGER);
            option.setValue(String.valueOf(count));
            data.getOptions().put(option.getName(), option);
        }
        return data;
    }

}
