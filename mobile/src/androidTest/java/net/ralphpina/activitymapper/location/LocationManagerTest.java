package net.ralphpina.activitymapper.location;

import android.support.test.rule.ActivityTestRule;

import net.ralphpina.activitymapper.AMApplication;
import net.ralphpina.activitymapper.BaseTest;
import net.ralphpina.activitymapper.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LocationManagerTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    // === connecting ==============================================================================

    @Test
    public void user_not_logged_in_does_not_connect_apis() throws Exception {
        LocationManager locationManager = AMApplication.get().locationManager();
        assertThat(locationManager.isTestClientConnected(), is(false));
        assertThat(locationManager.isTestLocationConnected(), is(false));
        assertThat(locationManager.isTestActivityConnected(), is(false));
    }

    @Test
    public void user_logged_in_has_connected_apis() throws Exception {
        connectTestUser();
        LocationManager locationManager = AMApplication.get().locationManager();
        locationManager.connect();
        assertThat(locationManager.isTestClientConnected(), is(true));
        assertThat(locationManager.isTestLocationConnected(), is(true));
        assertThat(locationManager.isTestActivityConnected(), is(true));
    }
}
