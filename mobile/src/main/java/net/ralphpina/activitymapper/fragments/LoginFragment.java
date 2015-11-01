package net.ralphpina.activitymapper.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import net.ralphpina.activitymapper.AMApplication;
import net.ralphpina.activitymapper.R;
import net.ralphpina.activitymapper.events.NavigateToMapEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginFragment extends Fragment {

    public final static String TAG = "LoginFragment";

    @Bind(R.id.edit_text_username)
    EditText _editTextUsername;
    @Bind(R.id.edit_text_password)
    EditText _editTextPassword;
    @Bind(R.id.edit_text_password_again)
    EditText _editTextPasswordAgain;
    @Bind(R.id.button_login)
    Button   _buttonLogin;
    @Bind(R.id.button_sign_up)
    Button   _buttonSignup;

    private boolean _isSigningUp;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.button_login)
    public void onLoginClick() {
        login();
    }

    @OnClick(R.id.button_sign_up)
    public void onSignUpClick() {
        if (_isSigningUp) {
            _isSigningUp = false;
            _buttonLogin.setText(R.string.login);
            _buttonSignup.setText(R.string.signup);
            _editTextPasswordAgain.setVisibility(GONE);
        } else {
            _isSigningUp = true;
            _buttonLogin.setText(R.string.signup);
            _buttonSignup.setText(R.string.login);
            _editTextPasswordAgain.setVisibility(VISIBLE);
        }
    }

    private void login() {
        // hide keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(_editTextUsername.getWindowToken(), 0);

        String username = _editTextUsername.getText()
                                           .toString()
                                           .trim();
        String password = _editTextPassword.getText()
                                           .toString()
                                           .trim();
        String passwordAgain = _editTextPasswordAgain.getText()
                                                     .toString()
                                                     .trim();

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (_isSigningUp) {
            if (!password.equals(passwordAgain)) {
                if (validationError) {
                    validationErrorMessage.append(getString(R.string.error_join));
                }
                validationError = true;
                validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
            }
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Snackbar.make(_buttonLogin, validationErrorMessage.toString(), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        if (_isSigningUp) {
            dialog.setMessage(getString(R.string.progress_signup));
        } else {
            dialog.setMessage(getString(R.string.progress_login));
        }
        dialog.show();

        if (_isSigningUp) {
            // Set up a new Parse user
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);

            // Call the Parse signup method
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    dialog.dismiss();
                    if (e != null) {
                        // Show the error message
                        Snackbar.make(_buttonLogin,
                                      e.getMessage(),
                                      Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        // Start an intent for the dispatch activity
                        EventBus.getDefault()
                                .post(new NavigateToMapEvent());
                    }
                }
            });
        } else {
            // TODO - I should use Dagger 2 for this, otherwise, I will need to mock out
            // TODO - all of parse
//            if (((AMApplication) getActivity().getApplicationContext()).isTestEnvironment()) {
//                Snackbar.make(_buttonLogin,
//                              "Yes, we are in test land",
//                              Snackbar.LENGTH_LONG)
//                        .show();
//            } else {
            // Call the Parse login method
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    dialog.dismiss();
                    if (e != null) {
                        // Show the error message
                        Snackbar.make(_buttonLogin,
                                      e.getMessage(),
                                      Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        // Start an intent for the dispatch activity
                        AMApplication.get()
                                     .locationManager()
                                     .connect();
                        EventBus.getDefault()
                                .post(new NavigateToMapEvent());
                    }
                }
            });
//            }
        }
    }
}
