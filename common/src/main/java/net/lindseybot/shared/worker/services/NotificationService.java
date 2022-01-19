package net.lindseybot.shared.worker.services;

import net.dv8tion.jda.api.entities.Guild;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.entities.Notification;

import java.util.List;

public interface NotificationService {

    void notify(Guild guild, Label message);

    List<Notification> findRecent(Guild guild);

}
