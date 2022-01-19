package net.lindseybot.shared.worker.reference;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
@NoArgsConstructor
public class CommandReference {

    private Object instance;
    private Method method;

    private boolean nsfw;
    private boolean ephemeral;

    public void invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        this.method.invoke(instance, args);
    }

}
