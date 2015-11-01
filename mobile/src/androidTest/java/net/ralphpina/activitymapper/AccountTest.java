package net.ralphpina.activitymapper;

import android.support.test.rule.ActivityTestRule;

import net.ralphpina.activitymapper.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;

public class AccountTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


}
