package net.lindseybot.shared.worker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionUtils {

    private static final Pattern boolPattern =
            Pattern.compile("(1|t(?:rue)?|enabled?|on)", Pattern.CASE_INSENSITIVE);

    public static Boolean parseBoolean(String text) {
        Matcher matcher = boolPattern.matcher(text);
        return matcher.find();
    }

}
