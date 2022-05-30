package net.lindseybot.shared.worker.impl;

import io.prometheus.client.Summary;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.GenericAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.lindseybot.shared.entities.discord.Label;
import net.lindseybot.shared.worker.InteractionService;
import net.lindseybot.shared.worker.Metrics;
import net.lindseybot.shared.worker.UserError;
import net.lindseybot.shared.worker.legacy.FakeSlashCommand;
import net.lindseybot.shared.worker.reference.*;
import net.lindseybot.shared.worker.services.Messenger;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class DefaultInteractionListener extends ListenerAdapter {

    private final Messenger msg;
    private final Metrics metrics;
    private final InteractionService service;
    private final ThreadPoolTaskExecutor taskExecutor;

    public DefaultInteractionListener(InteractionService service,
                                      IEventManager api,
                                      Messenger msg,
                                      Metrics metrics) {
        this.service = service;
        this.msg = msg;
        api.register(this);
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("commands-");
        taskExecutor.initialize();
        this.metrics = metrics;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!this.service.hasCommand(event.getCommandPath())) {
            return;
        }
        CommandReference reference = this.service.getCommand(event.getCommandPath());
        if (reference.isGuildOnly() && !event.isFromGuild()) {
            return;
        } else if (reference.isNsfw() && !this.isNSFW(event.getChannel())) {
            this.msg.error(event, Label.of("error.nsfw"));
            return;
        }
        try {
            Summary.Child timer = this.metrics.commands(event.getName(), !(event instanceof FakeSlashCommand));
            this.taskExecutor.submit(() -> timer.observe(timer.time(() -> {
                try {
                    reference.invoke(event);
                } catch (UserError error) {
                    this.msg.error(event, error.getLabel());
                } catch (Exception ex) {
                    log.error("Failed to execute command: " + event.getCommandPath(), ex);
                    this.msg.error(event, Label.of("error.internal"));
                }
            }))).get(1500, TimeUnit.MILLISECONDS);
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
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
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
                } catch (UserError error) {
                    this.msg.error(event, error.getLabel());
                } catch (Exception ex) {
                    log.error("Failed to execute button: " + event.getComponentId(), ex);
                    this.msg.error(event, Label.of("error.internal"));
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
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        String id = event.getComponentId();
        if (id.contains(":")) {
            id = id.split(":")[0];
        }
        if (!this.service.hasSelectMenu(id)) {
            return;
        }
        SelectMenuReference reference = this.service.getSelectMenu(id);
        try {
            this.taskExecutor.submit(() -> {
                try {
                    reference.invoke(event);
                } catch (UserError error) {
                    this.msg.error(event, error.getLabel());
                } catch (Exception ex) {
                    log.error("Failed to execute select menu: " + event.getComponentId(), ex);
                    this.msg.error(event, Label.of("error.internal"));
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

    @Override
    public void onGenericAutoCompleteInteraction(@NotNull GenericAutoCompleteInteractionEvent event) {
        if (event instanceof
                CommandAutoCompleteInteractionEvent commandEvent) {
            String cmdPath = commandEvent.getCommandPath();
            String optName = commandEvent.getFocusedOption().getName();
            String path = (cmdPath + "/" + optName).replace("/", ".");
            if (!this.service.hasAutoComplete(path)) {
                return;
            }
            AutoCompleteReference reference = this.service.getAutoComplete(path);
            if (!reference.isCommand()) {
                return;
            }
            try {
                reference.invoke(commandEvent);
            } catch (Exception ex) {
                log.error("Failed to execute autocomplete", ex);
            }
        } else {
            log.warn("Invalid autocomplete event: " + event.getClass().getSimpleName());
        }
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        String id = event.getCommandPath();
        if (!this.service.hasMessageCommand(id)) {
            return;
        }
        MessageCommandReference reference = this.service.getMessageCommand(id);
        try {
            this.taskExecutor.submit(() -> {
                try {
                    reference.invoke(event);
                } catch (UserError error) {
                    this.msg.error(event, error.getLabel());
                } catch (Exception ex) {
                    log.error("Failed to execute message command: " + id, ex);
                    this.msg.error(event, Label.of("error.internal"));
                }
            }).get(1500, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to schedule message command execution", e);
        } catch (TimeoutException e) {
            if (event.isAcknowledged()) {
                return;
            }
            log.warn("Timed out during message execution: {}", id);
            event.deferReply(reference.isEphemeral())
                    .queue();
        }
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        String id = event.getCommandPath();
        if (!this.service.hasUserCommand(id)) {
            return;
        }
        UserCommandReference reference = this.service.getUserCommand(id);
        try {
            this.taskExecutor.submit(() -> {
                try {
                    reference.invoke(event);
                } catch (UserError error) {
                    this.msg.error(event, error.getLabel());
                } catch (Exception ex) {
                    log.error("Failed to execute user command: " + id, ex);
                    this.msg.error(event, Label.of("error.internal"));
                }
            }).get(1500, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to schedule user command execution", e);
        } catch (TimeoutException e) {
            if (event.isAcknowledged()) {
                return;
            }
            log.warn("Timed out during user execution: {}", id);
            event.deferReply(reference.isEphemeral())
                    .queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String id = event.getModalId();
        if (!this.service.hasModal(id)) {
            return;
        }
        ModalReference reference = this.service.getModal(id);
        try {
            this.taskExecutor.submit(() -> {
                try {
                    reference.invoke(event);
                } catch (UserError error) {
                    this.msg.error(event, error.getLabel());
                } catch (Exception ex) {
                    log.error("Failed to execute user command: " + id, ex);
                    this.msg.error(event, Label.of("error.internal"));
                }
            }).get(1500, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to schedule user command execution", e);
        } catch (TimeoutException e) {
            if (event.isAcknowledged()) {
                return;
            }
            log.warn("Timed out during user execution: {}", id);
            event.deferReply(reference.isEphemeral())
                    .queue();
        }
    }

    private boolean isNSFW(MessageChannel channel) {
        if (channel instanceof BaseGuildMessageChannel guild) {
            return guild.isNSFW();
        } else if (channel instanceof ThreadChannel thread) {
            return ((BaseGuildMessageChannel) thread.getParentMessageChannel()).isNSFW();
        } else {
            return false;
        }
    }

}
