package net.lindseybot.shared.worker.impl;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class PooledEventManager extends InterfacedEventManager {

    private List<Object> listeners = new ArrayList<>();
    private final ThreadPoolExecutor threadExecutor;

    public PooledEventManager() {
        CountingThreadFactory factory = new CountingThreadFactory(() -> "JDA", "EventManager", false);
        this.threadExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.threadExecutor.setThreadFactory(factory);
    }

    @Override
    public void register(@NotNull Object listener) {
        super.register(listener);
        this.listeners = this.getRegisteredListeners();
    }

    @Override
    public void unregister(@NotNull Object listener) {
        super.unregister(listener);
        this.listeners = this.getRegisteredListeners();
    }

    @Override
    public void handle(@NotNull GenericEvent event) {
        threadExecutor.submit(() -> {
            List<Object> listeners = this.getListeners();
            for (Object listener : listeners) {
                try {
                    ((EventListener) listener).onEvent(event);
                } catch (Exception exception) {
                    log.error("Uncaught Exception on Listener", exception);
                }
            }
        });
    }

    private List<Object> getListeners() {
        return this.listeners;
    }

}
