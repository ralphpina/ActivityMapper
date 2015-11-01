package net.ralphpina.activitymapper;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.parse.ParseException;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaseTest {

    @Before
    public void setUp() {
        ParseUser.logOut();
    }

    protected void connectTestUser() {
        try {
            ParseUser.logIn("testuser", "Test1234");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
