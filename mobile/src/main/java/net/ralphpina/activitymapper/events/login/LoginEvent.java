package net.ralphpina.activitymapper.events.login;

import com.parse.ParseException;

public class LoginEvent {

    private final ParseException mParseException;

    public LoginEvent(ParseException parseException) {
        mParseException = parseException;
    }

    public ParseException getParseException() {
        return mParseException;
    }
}
