package net.lindseybot.shared.worker.impl;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.GenericAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.lindseybot.shared.worker.*;
import net.lindseybot.shared.worker.reference.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultInteractionService implements InteractionService {

    private final Map<String, CommandReference> commands = new HashMap<>();
    private final Map<String, ButtonReference> buttons = new HashMap<>();
    private final Map<String, SelectMenuReference> selects = new HashMap<>();
    private final Map<String, AutoCompleteReference> autoCompletes = new HashMap<>();
    private final Map<String, UserCommandReference> userCommands = new HashMap<>();
    private final Map<String, MessageCommandReference> messageCommands = new HashMap<>();
    private final Map<String, ModalReference> modals = new HashMap<>();

    public DefaultInteractionService(List<InteractionHandler> handlers) {
        handlers.forEach(this::register);
    }

    @Override
    public void register(InteractionHandler handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            this.registerCommands(method, handler);
            this.registerButtons(method, handler);
            this.registerSelects(method, handler);
            this.registerAutoCompletes(method, handler);
            this.registerUserCommands(method, handler);
            this.registerMessageCommands(method, handler);
            this.registerModals(method, handler);
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
    public boolean hasAutoComplete(String path) {
        return this.autoCompletes.containsKey(path);
    }

    @Override
    public boolean hasUserCommand(String name) {
        return this.userCommands.containsKey(name);
    }

    @Override
    public boolean hasMessageCommand(String name) {
        return this.messageCommands.containsKey(name);
    }

    @Override
    public boolean hasModal(String path) {
        return this.modals.containsKey(path);
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

    @Override
    public AutoCompleteReference getAutoComplete(String path) {
        return this.autoCompletes.get(path);
    }

    @Override
    public UserCommandReference getUserCommand(String name) {
        return this.userCommands.get(name);
    }

    @Override
    public MessageCommandReference getMessageCommand(String name) {
        return this.messageCommands.get(name);
    }

    @Override
    public ModalReference getModal(String path) {
        return this.modals.get(path);
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

    private void registerModals(Method method, InteractionHandler handler) {
        ModalListener mdl = method.getDeclaredAnnotation(ModalListener.class);
        if (mdl == null) {
            return;
        } else if (method.getParameterCount() == 0) {
            log.warn("Invalid modal listener declaration: " + mdl.value());
            return;
        } else if (!ModalInteractionEvent.class.equals(method.getParameterTypes()[0])) {
            log.warn("Invalid modal listener declaration: " + mdl.value());
            return;
        }
        ModalReference reference = new ModalReference();
        reference.setInstance(handler);
        reference.setMethod(method);
        reference.setEdit(mdl.edit());
        reference.setEphemeral(mdl.ephemeral());
        modals.put(mdl.value(), reference);
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

    private void registerAutoCompletes(Method method, InteractionHandler handler) {
        AutoComplete ac = method.getDeclaredAnnotation(AutoComplete.class);
        if (ac == null) {
            return;
        } else if (method.getParameterCount() == 0) {
            log.warn("Invalid auto complete listener declaration: " + ac.value());
            return;
        }
        if (!ac.command()
                && !GenericAutoCompleteInteractionEvent.class.equals(method.getParameterTypes()[0])) {
            log.warn("Invalid auto complete listener declaration: " + ac.value());
            return;
        } else if (ac.command() &&
                !GenericAutoCompleteInteractionEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
            log.warn("Invalid command auto complete listener declaration: " + ac.value());
            return;
        }
        AutoCompleteReference reference = new AutoCompleteReference();
        reference.setInstance(handler);
        reference.setMethod(method);
        reference.setCommand(ac.command());
        autoCompletes.put(ac.value(), reference);
    }

    private void registerUserCommands(Method method, InteractionHandler handler) {
        UserCommand uc = method.getDeclaredAnnotation(UserCommand.class);
        if (uc == null) {
            return;
        } else if (method.getParameterCount() == 0) {
            log.warn("Invalid user command listener declaration: " + uc.value());
            return;
        } else if (!UserContextInteractionEvent.class.equals(method.getParameterTypes()[0])) {
            log.warn("Invalid user command listener declaration: " + uc.value());
            return;
        }
        UserCommandReference reference = new UserCommandReference();
        reference.setInstance(handler);
        reference.setMethod(method);
        reference.setEphemeral(uc.ephemeral());
        userCommands.put(uc.value(), reference);
    }

    private void registerMessageCommands(Method method, InteractionHandler handler) {
        MessageCommand mc = method.getDeclaredAnnotation(MessageCommand.class);
        if (mc == null) {
            return;
        } else if (method.getParameterCount() == 0) {
            log.warn("Invalid message command listener declaration: " + mc.value());
            return;
        } else if (!MessageContextInteractionEvent.class.equals(method.getParameterTypes()[0])) {
            log.warn("Invalid message command listener declaration: " + mc.value());
            return;
        }
        MessageCommandReference reference = new MessageCommandReference();
        reference.setInstance(handler);
        reference.setMethod(method);
        reference.setEphemeral(mc.ephemeral());
        messageCommands.put(mc.value(), reference);
    }

}
