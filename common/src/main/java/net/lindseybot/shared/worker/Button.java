package net.lindseybot.shared.worker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to register a Button handler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Button {

    /**
     * Listening path for this button.
     *
     * @return path.
     */
    String value();

    /**
     * If this button's reply is an edit or a reply.
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
