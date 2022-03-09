package net.lindseybot.shared.worker.reference;

import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public class MessageCommandReference {

    private Object instance;
    private Method method;

    private boolean ephemeral;

    public void invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        this.method.invoke(instance, args);
    }

}
