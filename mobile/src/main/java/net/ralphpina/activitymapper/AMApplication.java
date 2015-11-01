package net.ralphpina.activitymapper;

import android.app.Application;

import com.parse.Parse;

public class AMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_application_id),
                         getString(R.string.parse_client_key));
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
