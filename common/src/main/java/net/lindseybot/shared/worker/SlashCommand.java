package net.lindseybot.shared.worker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SlashCommand {

    /**
     * Listening path for this command. Using dots.
     *
     * @return path.
     */
    String value();

    /**
     * If this command can only be used in NSFW channels.
     *
     * @return nsfw flag.
     */
    boolean nsfw() default false;

    /**
     * If this execution is ephemeral, in case it times out.
     *
     * @return ephemeral flag.
     */
    boolean ephemeral() default false;

}
