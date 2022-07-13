package net.lindseybot.bot.internal;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.HashSet;
import java.util.Set;

public class NetworkExceptions extends AbstractMatcherFilter<ILoggingEvent> {

    private final Set<String> messages = new HashSet<>();

    public NetworkExceptions() {
        messages.add("connection reset");
        messages.add("connect timed");
        messages.add("read timed");
        messages.add("network is unreachable");
        messages.add("timeout");
        messages.add("http error fetching url. status=503");
        messages.add("unknown interaction");
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        IThrowableProxy proxy = event.getThrowableProxy();
        if (proxy != null) {
            String msg = proxy.getMessage();
            if (this.isBlocked(msg)) {
                return FilterReply.DENY;
            }
        }
        if (this.isBlocked(event.getMessage())) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }

    private boolean isBlocked(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        for (String str : messages) {
            if (lower.contains(str)) {
                return true;
            }
        }
        return false;
    }

}
