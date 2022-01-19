package net.lindseybot.shared.worker.impl;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.lindseybot.shared.worker.reference.ButtonReference;
import net.lindseybot.shared.worker.reference.CommandReference;
import net.lindseybot.shared.worker.reference.SelectMenuReference;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class DefaultInteractionListener extends ListenerAdapter {

    private final DefaultInteractionService service;
    private final ThreadPoolTaskExecutor taskExecutor;

    public DefaultInteractionListener(DefaultInteractionService service, ShardManager api) {
        this.service = service;
        api.addEventListener(this);
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("commands-");
        taskExecutor.initialize();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!this.service.hasCommand(event.getCommandPath())) {
            return;
        }
        // TODO: Permission check
        // TODO: Module check
        CommandReference reference = this.service.getCommand(event.getCommandPath());
        if (reference.isNsfw() && !event.getTextChannel().isNSFW()) {
            event.reply("This command is NSFW. It can only be executed in NSFW channels.")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        try {
            this.taskExecutor.submit(() -> {
                try {
                    reference.invoke(event);
                } catch (Exception ex) {
                    log.error("Failed to execute command: " + event.getCommandPath(), ex);
                }
            }).get(1500, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to schedule command execution", e);
        } catch (TimeoutException e) {
            if (event.isAcknowledged()) {
                return;
            }
            log.warn("Timed out during command execution: {}", event.getCommandPath());
            event.deferReply(reference.isEphemeral())
                    .queue();
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        String id = event.getComponentId();
        if (id.contains(":")) {
            id = id.split(":")[0];
        }
        if (!this.service.hasButton(id)) {
            return;
        }
        ButtonReference reference = this.service.getButton(id);
        try {
            this.taskExecutor.submit(() -> {
                try {
                    reference.invoke(event);
                } catch (Exception ex) {
                    log.error("Failed to execute button: " + event.getComponentId(), ex);
                }
            }).get(1500, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to schedule button execution", e);
        } catch (TimeoutException e) {
            if (event.isAcknowledged()) {
                return;
            }
            log.warn("Timed out during button execution: {}", event.getComponentId());
            if (reference.isEdit()) {
                event.deferEdit()
                        .queue();
            } else {
                event.deferReply(reference.isEdit())
                        .queue();
            }
        }
    }

    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        if (!this.service.hasSelectMenu(event.getComponentId())) {
            return;
        }
        SelectMenuReference reference = this.service.getSelectMenu(event.getComponentId());
        try {
            this.taskExecutor.submit(() -> {
                try {
                    reference.invoke(event);
                } catch (Exception ex) {
                    log.error("Failed to execute select menu: " + event.getComponentId(), ex);
                }
            }).get(1500, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to schedule select menu execution", e);
        } catch (TimeoutException e) {
            if (event.isAcknowledged()) {
                return;
            }
            log.warn("Timed out during select menu execution: {}", event.getComponentId());
            if (reference.isEdit()) {
                event.deferEdit()
                        .queue();
            } else {
                event.deferReply(reference.isEdit())
                        .queue();
            }
        }
    }

}
