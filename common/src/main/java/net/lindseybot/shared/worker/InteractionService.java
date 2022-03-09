package net.lindseybot.shared.worker;

import net.lindseybot.shared.worker.reference.*;

public interface InteractionService {

    void register(InteractionHandler interactionHandler);

    boolean hasCommand(String path);

    boolean hasButton(String path);

    boolean hasSelectMenu(String path);

    boolean hasAutoComplete(String path);

    boolean hasUserCommand(String name);

    boolean hasMessageCommand(String name);

    CommandReference getCommand(String path);

    ButtonReference getButton(String path);

    SelectMenuReference getSelectMenu(String path);

    AutoCompleteReference getAutoComplete(String path);

    UserCommandReference getUserCommand(String name);

    MessageCommandReference getMessageCommand(String name);

}
