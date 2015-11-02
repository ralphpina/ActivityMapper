package net.ralphpina.activitymapper.location;

import net.ralphpina.activitymapper.Account;
import net.ralphpina.activitymapper.BaseTest;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocationManagerTest extends BaseTest {

    @Test
    public void connected_does_nothing_if_user_is_logged_out() throws Exception {
        {
            assertThat(Account.get()
                              .isUserVerified(), is(false));
        }

        LocationManager locationManager = new LocationManager();
        {
            assertThat(locationManager.isTestClientConnected(), is(false));
            assertThat(locationManager.isTestActivityConnected(), is(false));
            assertThat(locationManager.isTestLocationConnected(), is(false));
        }
    }

    @Test
    public void connects_when_user_is_verified() throws Exception {
        connectTestUser();
        {
            assertThat(Account.get()
                              .isUserVerified(), is(true));
        }

        LocationManager locationManager = new LocationManager();
        {
            assertThat(locationManager.isTestClientConnected(), is(true));
            assertThat(locationManager.isTestActivityConnected(), is(true));
            assertThat(locationManager.isTestLocationConnected(), is(true));
        }
    }
}