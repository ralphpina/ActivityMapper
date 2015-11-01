package net.ralphpina.activitymapper;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaseTest {

    @Before
    public void setUp() {
        Account.get().logout();
    }

    protected void connectTestUser() {
        Account.get().login(false, "testuser", "Test1234");
    }
}
