package net.lindseybot.shared.worker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to register a Modal handler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ModalListener {

    /**
     * Listening path for this modal.
     *
     * @return path.
     */
    String value();

    /**
     * If this modal's reply is an edit or a reply.
     *
     * @return edit flag.
     */
    boolean edit() default true;

    /**
     * If this execution is ephemeral, in case it times out.
     *
     * @return ephemeral flag.
     */
    boolean ephemeral() default false;

}
