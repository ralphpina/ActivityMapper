package net.ralphpina.activitymapper.events.login;

import com.parse.ParseException;

public class LogoutEvent {

    private final ParseException mParseException;

    public LogoutEvent(ParseException parseException) {
        mParseException = parseException;
    }

    public ParseException getParseException() {
        return mParseException;
    }
}
