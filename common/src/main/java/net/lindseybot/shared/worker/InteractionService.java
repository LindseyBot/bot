package net.lindseybot.shared.worker;

import net.lindseybot.shared.worker.reference.AutoCompleteReference;
import net.lindseybot.shared.worker.reference.ButtonReference;
import net.lindseybot.shared.worker.reference.CommandReference;
import net.lindseybot.shared.worker.reference.SelectMenuReference;

public interface InteractionService {

    void register(InteractionHandler interactionHandler);

    boolean hasCommand(String path);

    boolean hasButton(String path);

    boolean hasSelectMenu(String path);

    boolean hasAutoComplete(String path);

    CommandReference getCommand(String path);

    ButtonReference getButton(String path);

    SelectMenuReference getSelectMenu(String path);

    AutoCompleteReference getAutoComplete(String path);
}
