package net.lindseybot.shared.worker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to register a Message Context Command handler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageCommand {

    /**
     * Listening path for this command handler.
     *
     * @return path.
     */
    String value();

    /**
     * If the response to this command is ephemeral by default.
     *
     * @return ephemeral.
     */
    boolean ephemeral() default false;

}
