package net.lindseybot.shared.worker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to register an AutoComplete handler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoComplete {

    /**
     * Listening path for this auto-complete handler.
     *
     * @return path.
     */
    String value();

    /**
     * If this auto complete handler is for handling slash command options.
     *
     * @return is slash command ready.
     */
    boolean command() default true;

}
