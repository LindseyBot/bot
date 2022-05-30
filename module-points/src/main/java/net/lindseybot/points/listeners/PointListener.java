package net.lindseybot.points.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.lindseybot.shared.entities.profile.servers.PointConfig;
import net.lindseybot.points.services.PointService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class PointListener extends ListenerAdapter {

    private final PointService service;

    public PointListener(IEventManager api, PointService service) {
        this.service = service;
        api.register(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getMember() == null) {
            return;
        } else if (event.getAuthor().isBot()) {
            return;
        } else if (event.isWebhookMessage()) {
            return;
        }
        PointConfig config = this.service.getConfig(event.getGuild());
        if (!config.isEnabled()) {
            return;
        }
        long points = config.getWeightMessage();
        Message message = event.getMessage();
        long count = message.getAttachments().size();
        if (count > 0) {
            points = points + config.getWeightAttachment() * count;
        }
        this.service.add(event.getMember(), points);
    }

}
