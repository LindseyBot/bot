package net.lindseybot.shared.worker.impl;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class PooledEventManager implements IEventManager {

    private final CopyOnWriteArrayList<Object> listeners = new CopyOnWriteArrayList<>();
    private final ThreadPoolExecutor threadExecutor;

    public PooledEventManager() {
        CountingThreadFactory factory = new CountingThreadFactory(() -> "JDA", "EventManager", false);
        this.threadExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.threadExecutor.setThreadFactory(factory);
        this.threadExecutor.setMaximumPoolSize(1024);
    }

    @Override
    public void register(@NotNull Object listener) {
        if (!(listener instanceof EventListener)) {
            throw new IllegalArgumentException("Listener must implement EventListener");
        }
        listeners.add(listener);
    }

    @Override
    public void unregister(@NotNull Object listener) {
        if (!(listener instanceof EventListener)) {
            return;
        }
        listeners.remove(listener);
    }

    @Override
    public void handle(@NotNull GenericEvent event) {
        if (event instanceof GenericInteractionCreateEvent) {
            threadExecutor.submit(() -> this.execute(event));
        } else if (event instanceof MessageReceivedEvent) {
            threadExecutor.submit(() -> this.execute(event));
        } else if (event instanceof GuildMemberJoinEvent) {
            threadExecutor.submit(() -> this.execute(event));
        } else if (event instanceof MessageReactionAddEvent) {
            threadExecutor.submit(() -> this.execute(event));
        } else if (event instanceof MessageReactionRemoveEvent) {
            threadExecutor.submit(() -> this.execute(event));
        } else {
            this.execute(event);
        }
    }

    private void execute(GenericEvent event) {
        List<Object> listeners = this.listeners;
        for (Object listener : listeners) {
            try {
                ((EventListener) listener).onEvent(event);
            } catch (Exception exception) {
                log.error("Uncaught Exception on Listener", exception);
            }
        }
    }

    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return List.copyOf(listeners);
    }

}
