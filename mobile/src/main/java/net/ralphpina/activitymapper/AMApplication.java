package net.ralphpina.activitymapper;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import net.ralphpina.activitymapper.location.Record;

public class AMApplication extends Application {

    private static AMApplication _this;
    private        Account       mAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        _this = this;

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Record.class);
        Parse.initialize(this, getString(R.string.parse_application_id),
                         getString(R.string.parse_client_key));

        mAccount = new Account(this);
    }

    public static AMApplication get() {
        return _this;
    }

    public boolean isTestEnvironment() {
        boolean result;
        try {
            getClassLoader()
                    .loadClass("net.ralphpina.activitymapper.TestIndicator");
            result = true;
        } catch (final Exception e) {
            result = false;
        }
        return result;
    }
}
