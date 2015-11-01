package net.ralphpina.activitymapper;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

public class LoginFragmentTest extends BaseTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void correct_ui() throws Exception {
        onView(withId(R.id.edit_text_password_again)).check(matches(not(isDisplayed())));
        onView(withId(R.id.button_sign_up)).check(matches(withText(
                "Sign up")))
                                           .perform(click())
                                           .check(matches(withText("Log in")));
        onView(withId(R.id.edit_text_password_again)).check(matches(isDisplayed()));
    }

    @Test
    public void clicking_login_without_user_name_password() throws Exception {
        onView(withId(R.id.button_login)).perform(click());
        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(withText(
                "Please enter a username, and enter a password.")))
                                                                 .check(matches(isDisplayed()));
    }

//    @Test
//    public void check_we_are_in_text_land() throws Exception {
//        onView(withId(R.id.edit_text_username)).perform(typeText("ralph.pina@gmail.com"));
//        onView(withId(R.id.edit_text_password)).perform(typeText("SomePassword"));
//        onView(withId(R.id.button_login)).perform(click());
//        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(withText(
//                "Yes, we are in test land")))
//                                                                 .check(matches(isDisplayed()));
//    }
}