package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.legacy.services.FinderUtil;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.legacy.fake.FakeOptionMapping;
import net.lindseybot.legacy.fake.FakeSlashData;
import net.lindseybot.shared.worker.services.Messenger;
import org.springframework.stereotype.Component;

@Component
public class ProfileConverter extends SlashConverter {

    private final Messenger msg;

    public ProfileConverter(Messenger msg) {
        this.msg = msg;
    }

    @Override
    public String[] getNames() {
        return new String[]{"profile"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        if (args.length == 0) {
            return this.createData(event, "profile");
        } else {
            Member target = FinderUtil.findMember(args[0], event.getGuild());
            if (target == null) {
                this.msg.reply(event, Label.raw("User not found."));
                return null;
            }
            FakeSlashData data = this.createData(event, "profile");
            {
                FakeOptionMapping option = new FakeOptionMapping();
                option.setName("target");
                option.setType(OptionType.USER);
                option.setValue(target.getId());
                data.getOptions().put(option.getName(), option);
            }
            return data;
        }
    }

}
