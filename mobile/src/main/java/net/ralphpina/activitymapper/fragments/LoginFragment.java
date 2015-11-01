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

import net.ralphpina.activitymapper.Account;
import net.ralphpina.activitymapper.R;
import net.ralphpina.activitymapper.events.EventBusMethod;
import net.ralphpina.activitymapper.events.login.LoginEvent;
import net.ralphpina.activitymapper.events.navigation.NavigateToMapEvent;

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
    private ProgressDialog mDialog;

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

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault()
                .register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault()
                .unregister(this);
        super.onPause();
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
        mDialog = new ProgressDialog(getActivity());
        if (_isSigningUp) {
            mDialog.setMessage(getString(R.string.progress_signup));
        } else {
            mDialog.setMessage(getString(R.string.progress_login));
        }
        mDialog.show();
        Account.get()
               .login(_isSigningUp, username, password);
    }

    // ===== EVENTS ================================================================================

    @EventBusMethod
    public void onEventMainThread(LoginEvent event) {
        mDialog.dismiss();
        mDialog = null;
        if (event.getParseException() != null) {
            // Show the error message
            Snackbar.make(_buttonLogin,
                          event.getParseException().getMessage(),
                          Snackbar.LENGTH_LONG)
                    .show();
        } else {
            EventBus.getDefault()
                    .post(new NavigateToMapEvent());
        }
    }
}
