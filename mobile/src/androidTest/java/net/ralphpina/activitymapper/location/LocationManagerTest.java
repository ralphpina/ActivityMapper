package net.ralphpina.activitymapper.location;

import android.support.test.rule.ActivityTestRule;

import net.ralphpina.activitymapper.BaseTest;
import net.ralphpina.activitymapper.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;

public class LocationManagerTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    // === connecting ==============================================================================


}
