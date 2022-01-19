package net.lindseybot.fun.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.lindseybot.fun.entities.StarboardMessage;
import net.lindseybot.fun.repositories.sql.StarboardMessageRepository;
import net.lindseybot.fun.repositories.sql.StarboardRepository;
import net.lindseybot.shared.entities.profile.servers.Starboard;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StarboardService {

    private final StarboardRepository repository;
    private final StarboardMessageRepository msgRepository;

    public StarboardService(StarboardRepository repository, StarboardMessageRepository msgRepository) {
        this.repository = repository;
        this.msgRepository = msgRepository;
    }

    @NotNull
    public StarboardMessage findMessage(MessageReaction reaction, Guild guild, TextChannel starboardChannel) {
        Optional<StarboardMessage> oStarboard;
        if (reaction.getChannel().getId().equals(starboardChannel.getId())) {
            oStarboard = msgRepository.findByMessageId(reaction.getMessageIdLong());
        } else {
            oStarboard = msgRepository.findById(reaction.getMessageIdLong());
        }
        if (oStarboard.isEmpty()) {
            StarboardMessage starboard = new StarboardMessage();
            starboard.setTargetId(reaction.getMessageIdLong());
            starboard.setChannelId(reaction.getChannel().getIdLong());
            starboard.setGuildId(guild.getIdLong());
            return starboard;
        } else {
            return oStarboard.get();
        }
    }

    public void delete(StarboardMessage message) {
        this.msgRepository.delete(message);
    }

    public void save(StarboardMessage starboard) {
        this.msgRepository.save(starboard);
    }

    public Starboard getSettings(Guild guild) {
        return this.repository.findById(guild.getIdLong())
                .orElse(new Starboard());
    }

    public TextChannel getChannel(Guild guild, Starboard settings) {
        if (!settings.isEnabled()) {
            return null;
        }
        Long channel = settings.getChannel();
        if (channel == null) {
            return null;
        } else {
            return guild.getTextChannelById(channel);
        }
    }

}
