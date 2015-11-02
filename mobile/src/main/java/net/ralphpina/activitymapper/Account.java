package net.ralphpina.activitymapper;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import net.ralphpina.activitymapper.events.login.LoginEvent;
import net.ralphpina.activitymapper.events.login.LogoutEvent;
import net.ralphpina.activitymapper.events.navigation.LocationChangedEvent;
import net.ralphpina.activitymapper.location.LocationTrackingService;
import net.ralphpina.activitymapper.location.Record;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

// TODO use Dagger2 to inject a different object during testing
public class Account {

    private static final String TAG = "Account";

    private static Account       mAccount;
    private        AMApplication mApplication;
    private        MockParse     mMockParse;
    private        int           mMostRecentMapUpdate;

    @VisibleForTesting
    protected Account() {
        mAccount = this;
        mMockParse = new MockParse();
    }

    public Account(AMApplication application) {
        mApplication = application;
        mAccount = this;
        if (isInstrumentationTest()) {
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
            if (isInstrumentationTest()) {
                LocationTrackingService.start();
                EventBus.getDefault()
                        .post(new LoginEvent(null));
            }
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
            if (isInstrumentationTest()) {
                LocationTrackingService.stop();
                EventBus.getDefault()
                        .post(new LogoutEvent(null));
            }
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
            mMockParse.addRecord(record);
        } else {
            record.saveEventually();
        }
        broadcastRecordsForLast24Hours();
    }

    public void broadcastRecordsForLast24Hours() {
        if (mMockParse != null) {
            List<Record> recordsForLast24Hours = new ArrayList<>();
            Date onDayAgo = getNowMinus24Hours();
            for (Record record : mMockParse.getRecords()) {
                if (record.getCreatedAt()
                          .before(onDayAgo)) {
                    recordsForLast24Hours.add(record);
                }
            }
            EventBus.getDefault()
                    .post(new LocationChangedEvent(recordsForLast24Hours));
        } else {
            queryParseForLast24Hours();
        }
    }

    private void queryParseForLast24Hours() {
        final int myUpdateNumber = ++mMostRecentMapUpdate;
        ParseQuery<Record> mapQuery = Record.getQuery();
        mapQuery.include("user");
        Date onDayAgo = getNowMinus24Hours();
        mapQuery.whereGreaterThanOrEqualTo("createdAt", onDayAgo);
        mapQuery.orderByDescending("createdAt");
        mapQuery.setLimit(100);
        // Kick off the query in the background
        mapQuery.findInBackground(new FindCallback<Record>() {
            @Override
            public void done(List<Record> records, ParseException e) {
                Log.e(TAG, "=== findInBackground === records = " + records);
                if (e != null) {
                    Log.e(TAG, "=== findInBackground === records = " + records);
                    return;
                }
                /*
                 * Make sure we're processing results from
                 * the most recent update, in case there
                 * may be more than one in progress.
                 */
                if (myUpdateNumber != mMostRecentMapUpdate) {
                    return;
                }
                EventBus.getDefault()
                        .post(new LocationChangedEvent(records));
            }
        });
    }

    @NonNull
    private Date getNowMinus24Hours() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
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

        public List<Record> getRecords() {
            return mRecords;
        }
    }

    private boolean isInstrumentationTest() {
        return mApplication != null && mApplication.isTestEnvironment();
    }

    public static boolean isTestEnvironment() {
        // in unit tests application will be null, however, it will be there
        // in instrumentation tests
        return AMApplication.get() == null || AMApplication.get()
                                                           .isTestEnvironment();
    }
}
