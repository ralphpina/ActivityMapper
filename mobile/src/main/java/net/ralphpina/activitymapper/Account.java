package net.ralphpina.activitymapper;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import net.ralphpina.activitymapper.events.login.LoginEvent;
import net.ralphpina.activitymapper.events.login.LogoutEvent;
import net.ralphpina.activitymapper.location.LocationTrackingService;
import net.ralphpina.activitymapper.location.Record;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

// TODO use Dagger2 to inject a different object during testing
public class Account {

    private static Account         mAccount;
    private        AMApplication   mApplication;
    private        MockParse       mMockParse;

    @VisibleForTesting
    protected Account() {
        mAccount = this;
    }

    public Account(AMApplication application) {
        mApplication = application;
        mAccount = this;
        if (mApplication.isTestEnvironment()) {
            mMockParse = new MockParse();
        }
        if (isUserVerified()) {
            LocationTrackingService.start();
        }
    }

    public static Account get() {
        return mAccount;
    }

    // ===== USER STATE ============================================================================

    public boolean isUserVerified() {
        if (mMockParse != null) {
            return mMockParse.isLoggedIn();
        }
        return ParseUser.getCurrentUser() != null;
    }

    public void login(boolean isSigningUp, @NonNull String username, @NonNull String password) {
        if (mMockParse != null) {
            mMockParse.setIsLoggedIn(true);
            LocationTrackingService.start();
            EventBus.getDefault()
                    .post(new LoginEvent(null));
        } else {
            if (isSigningUp) {
                // Set up a new Parse user
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);

                // Call the Parse signup method
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            EventBus.getDefault()
                                    .post(new LoginEvent(e));
                        } else {
                            LocationTrackingService.start();
                            EventBus.getDefault()
                                    .post(new LoginEvent(null));
                        }
                    }
                });
            } else {
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            EventBus.getDefault()
                                    .post(new LoginEvent(e));
                        } else {
                            LocationTrackingService.start();
                            EventBus.getDefault()
                                    .post(new LoginEvent(null));
                        }
                    }
                });
            }
        }
    }

    public void logout() {
        if (mMockParse != null) {
            mMockParse.setIsLoggedIn(false);
            LocationTrackingService.stop();
            EventBus.getDefault()
                    .post(new LogoutEvent(null));
        } else {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        EventBus.getDefault()
                                .post(new LogoutEvent(e));
                    } else {
                        LocationTrackingService.stop();
                        EventBus.getDefault()
                                .post(new LogoutEvent(null));
                    }
                }
            });
        }
    }

    // ===== LOCATION RECORDS ======================================================================

    public void addRecord(Record record) {
        if (mMockParse != null) {
            addRecord(record);
        } else {
            record.saveEventually();
        }
    }

    // ===== TESTING ===============================================================================

    private class MockParse {

        private List<Record> mRecords = new ArrayList<>();
        private boolean mIsLoggedIn;

        // ===== LOGGED IN =========================================================================

        public boolean isLoggedIn() {
            return mIsLoggedIn;
        }

        public void setIsLoggedIn(boolean isLoggedIn) {
            mIsLoggedIn = isLoggedIn;
        }

        // ===== RECORDS ===========================================================================

        public void addRecord(Record record) {
            mRecords.add(record);
        }

    }
}
