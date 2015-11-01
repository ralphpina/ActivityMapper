package net.ralphpina.activitymapper.location;

import android.support.test.runner.AndroidJUnit4;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import net.ralphpina.activitymapper.Account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocationTrackingServiceTest extends ServiceTestCase<LocationTrackingService> {

    public LocationTrackingServiceTest() {
        super(LocationTrackingService.class);
    }

    @Before
    public void setUp() {
        Account.get().logout();
    }

    protected void connectTestUser() {
        Account.get().login(false, "testuser", "Test1234");
    }

    @Test
    public void user_not_logged_in_does_not_connect_apis() throws Exception {
        assertThat(Account.get().isUserVerified(), is(false));
        assertThat(LocationTrackingService.isRunning(), is(false));
    }

    // TODO //-- Race condition for service, it fails when ran by itself,
    // TODO //-- yet passes when running with the rest in this class
    @Test
    public void user_logged_in_has_connected_apis() throws Exception {
        connectTestUser();
        {
            assertThat(LocationTrackingService.isRunning(), is(true));
        }

        // manually do the lifecycle
        LocationTrackingService.get().onCreate();
        {
            assertThat(LocationTrackingService.get()
                                              .locationManager()
                                              .isTestClientConnected(), is(true));
            assertThat(LocationTrackingService.get()
                                              .locationManager()
                                              .isTestLocationConnected(), is(true));
            assertThat(LocationTrackingService.get()
                                              .locationManager()
                                              .isTestActivityConnected(), is(true));
        }
    }
}
