package net.lindseybot.legacy.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.lindseybot.legacy.models.SlashConverter;
import net.lindseybot.legacy.services.LegacyService;
import net.lindseybot.shared.worker.legacy.FakeSlashData;
import org.springframework.stereotype.Component;

@Component
public class PrefixHandler extends SlashConverter {

    private final LegacyService service;

    public PrefixHandler(LegacyService service) {
        this.service = service;
    }

    @Override
    public String[] getNames() {
        return new String[]{"prefix"};
    }

    @Override
    public FakeSlashData convert(MessageReceivedEvent event, String name, String[] args) {
        Member member = event.getMember();
        if (member == null) {
            return null;
        } else if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            return null;
        }
        if (args.length == 0) {
            return null;
        } else if (args[0].equalsIgnoreCase("reset")) {
            service.setPrefix(event.getGuild(), null);
            event.getMessage()
                    .reply("Prefix reset. Prefix commands may stop working in the future.")
                    .mentionRepliedUser(false)
                    .queue();
        } else {
            service.setPrefix(event.getGuild(), args[0]);
            event.getMessage()
                    .reply("Prefix updated. Prefix commands may stop working in the future.")
                    .mentionRepliedUser(false)
                    .queue();
        }
        return null;
    }

}
