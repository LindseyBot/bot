package net.lindseybot.shared.worker.impl;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.lindseybot.shared.worker.*;
import net.lindseybot.shared.worker.reference.ButtonReference;
import net.lindseybot.shared.worker.reference.CommandReference;
import net.lindseybot.shared.worker.reference.SelectMenuReference;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultInteractionService implements InteractionService {

    private final Map<String, CommandReference> commands = new HashMap<>();
    private final Map<String, ButtonReference> buttons = new HashMap<>();
    private final Map<String, SelectMenuReference> selects = new HashMap<>();

    public DefaultInteractionService(List<InteractionHandler> handlers) {
        handlers.forEach(this::register);
    }

    @Override
    public void register(InteractionHandler handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            this.registerCommands(method, handler);
            this.registerButtons(method, handler);
            this.registerSelects(method, handler);
        }
    }

    @Override
    public boolean hasCommand(String path) {
        return this.commands.containsKey(path);
    }

    @Override
    public boolean hasButton(String path) {
        return this.buttons.containsKey(path);
    }

    @Override
    public boolean hasSelectMenu(String path) {
        return this.selects.containsKey(path);
    }

    @Override
    public CommandReference getCommand(String path) {
        return this.commands.get(path);
    }

    @Override
    public ButtonReference getButton(String path) {
        return this.buttons.get(path);
    }

    @Override
    public SelectMenuReference getSelectMenu(String path) {
        return this.selects.get(path);
    }

    private void registerCommands(Method method, InteractionHandler handler) {
        SlashCommand cmd = method.getDeclaredAnnotation(SlashCommand.class);
        if (cmd == null) {
            return;
        } else if (method.getParameterCount() == 0) {
            log.warn("Invalid command listener declaration: " + cmd.value());
            return;
        } else if (!SlashCommandInteractionEvent.class.equals(method.getParameterTypes()[0])) {
            log.warn("Invalid command listener declaration: " + cmd.value());
            return;
        }
        CommandReference reference = new CommandReference();
        reference.setInstance(handler);
        reference.setMethod(method);
        reference.setNsfw(cmd.nsfw());
        reference.setEphemeral(cmd.ephemeral());
        reference.setGuildOnly(cmd.guildOnly());
        commands.put(cmd.value().replace(".", "/"), reference);
    }

    private void registerButtons(Method method, InteractionHandler handler) {
        Button btn = method.getDeclaredAnnotation(Button.class);
        if (btn == null) {
            return;
        } else if (method.getParameterCount() == 0) {
            log.warn("Invalid button listener declaration: " + btn.value());
            return;
        } else if (!ButtonInteractionEvent.class.equals(method.getParameterTypes()[0])) {
            log.warn("Invalid button listener declaration: " + btn.value());
            return;
        }
        ButtonReference reference = new ButtonReference();
        reference.setInstance(handler);
        reference.setMethod(method);
        reference.setEdit(btn.edit());
        reference.setEphemeral(btn.ephemeral());
        buttons.put(btn.value(), reference);
    }

    private void registerSelects(Method method, InteractionHandler handler) {
        SelectMenu menu = method.getDeclaredAnnotation(SelectMenu.class);
        if (menu == null) {
            return;
        } else if (method.getParameterCount() == 0) {
            log.warn("Invalid select menu listener declaration: " + menu.value());
            return;
        } else if (!SelectMenuInteractionEvent.class.equals(method.getParameterTypes()[0])) {
            log.warn("Invalid select menu listener declaration: " + menu.value());
            return;
        }
        SelectMenuReference reference = new SelectMenuReference();
        reference.setInstance(handler);
        reference.setMethod(method);
        reference.setEdit(menu.edit());
        reference.setEphemeral(menu.ephemeral());
        selects.put(menu.value(), reference);
    }

}
