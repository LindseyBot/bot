package net.lindseybot.shared.worker.reference;

import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public class ModalReference {

    private Object instance;
    private Method method;

    private boolean edit;
    private boolean ephemeral;

    public void invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        this.method.invoke(instance, args);
    }

}
