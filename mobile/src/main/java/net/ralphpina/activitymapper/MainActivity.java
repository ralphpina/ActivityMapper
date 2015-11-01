package net.ralphpina.activitymapper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import net.ralphpina.activitymapper.events.EventBusMethod;
import net.ralphpina.activitymapper.events.NavigateToLoginEvent;
import net.ralphpina.activitymapper.events.NavigateToMapEvent;
import net.ralphpina.activitymapper.fragments.LoginFragment;
import net.ralphpina.activitymapper.fragments.MapFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    public final static int LOGIN  = 0;
    public final static int MAP    = 2;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({LOGIN,
             MAP})
    public @interface UiState {
    }

    public final static int FORWARD   = 0;
    public final static int BACKWARDS = 1;
    public final static int NONE      = 2;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({FORWARD,
             BACKWARDS,
             NONE})
    public @interface TransitionDirection {
    }

    @Bind(R.id.toolbar)
    Toolbar     _toolbar;

    @UiState
    private int _uiState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(_toolbar);

        _uiState = determineUiState();

        // if we rotate the app, or this Activity pauses then resumes,
        // the fragment attached to it will be maintained.
        if (savedInstanceState == null) {
            navigateToFragment(FORWARD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    // ===== MENU ==================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (_uiState == MAP) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.progress_logout));
            dialog.show();
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Snackbar.make(_toolbar,
                                      e.getMessage(),
                                      Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        AMApplication.get()
                                     .locationManager()
                                     .disconnect();
                        _uiState = LOGIN;
                        navigateToFragment(BACKWARDS);
                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ===== NAVIGATION ============================================================================

    @UiState
    private int determineUiState() {
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            return MAP;
        } else {
            return LOGIN;
        }
    }

    private void navigateToFragment(@TransitionDirection int direction) {
        invalidateOptionsMenu();
        Fragment fragment = null;
        String tag = null;
        if (_uiState == MAP) {
            fragment = new MapFragment();
            tag = MapFragment.TAG;
        } else if (_uiState == LOGIN) {
            fragment = new LoginFragment();
            tag = LoginFragment.TAG;
        }

        navigateToFragment(fragment, direction, tag);
    }

    private void navigateToFragment(Fragment fragment,
                                    @TransitionDirection int direction,
                                    String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (direction == FORWARD) {
            ft.setCustomAnimations(R.anim.slide_in_from_right,
                                   R.anim.slide_out_to_left);
        } else {
            ft.setCustomAnimations(R.anim.slide_in_from_left,
                                   R.anim.slide_out_to_right);
        }
        ft.replace(R.id.container, fragment, tag)
          .commit();
    }

    // ==== EVENTS =================================================================================

    @EventBusMethod
    public void onEventMainThread(NavigateToLoginEvent event) {
        _uiState = LOGIN;
        navigateToFragment(BACKWARDS);
    }

    @EventBusMethod
    public void onEventMainThread(NavigateToMapEvent event) {
        _uiState = MAP;
        navigateToFragment(FORWARD);
    }
}
