package com.example.peter.racemanager.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.peter.racemanager.R;
import com.example.peter.racemanager.fragments.TOSDialogFragment;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A VERY loose login implementation. Essentially sends information to MultiGP to see if we can
 * actually successfully login to the website with their information and return the MultiGP site
 * with the user's input username.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    public final static String GUEST = "AnonymousSpectator159753";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    // UI Stuff
    private FrameLayout layout;
    private RelativeLayout busyLayout;
    private TextView header;
    private EditText emailField;
    private EditText passwordField;
    private AppCompatCheckBox tosCheckbox;
    private TextView tosLink;
    private Button loginButton;
    private Button guestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Iconify.with(new FontAwesomeModule());
    }

    @Override
    protected void onStart() {
        super.onStart();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        layout = (FrameLayout) findViewById(R.id.login_layout);
        busyLayout = (RelativeLayout) findViewById(R.id.login_loadingPanel);
        header = (TextView) findViewById(R.id.login_guide);
        emailField = (EditText) findViewById(R.id.login_email);
        passwordField = (EditText) findViewById(R.id.login_password);
        tosCheckbox = (AppCompatCheckBox) findViewById(R.id.login_tos_checkbox);
        tosLink = (TextView) findViewById(R.id.login_tos_link);
        tosLink.setOnClickListener(this);
        loginButton = (Button) findViewById(R.id.login_submit);
        loginButton.setOnClickListener(this);
        guestButton = (Button) findViewById(R.id.login_guest);
        guestButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_submit:
                lockInterface();
                loginMultiGPUser();
                break;
            case R.id.login_guest:
                lockInterface();
                loginGuest();
                break;
            case R.id.login_tos_link:
                showToSDialog();
            default:
                Log.i("LOGIN ACTIVITY", "How did you get here?");
        }
    }

    // Locks the interface so that the user can't press anymore buttons or type into fields
    public void lockInterface() {
        loginButton.setEnabled(false);
        guestButton.setEnabled(false);
        busyLayout.setVisibility(View.VISIBLE);

        // Hide any keyboards that were used
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Unlocks the interface in the event that the user failed to login and needs to put new info
    public void unlockInterface() {
        loginButton.setEnabled(true);
        guestButton.setEnabled(true);
        busyLayout.setVisibility(View.GONE);
    }

    // Shows the Terms of Service when the view button is pressed
    public void showToSDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TOSDialogFragment tosDialogFragment = new TOSDialogFragment();
        tosDialogFragment.show(fm, "some_other_unknown_tag");
    }

    public void loginMultiGPUser() {
        if (tosCheckbox.isChecked()) {
            // Verify information
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            verifyUser(email, password);
        }
        else {
            // ToS not accepted
            askForToS();
        }
    }

    public void loginGuest() {
        if (tosCheckbox.isChecked()) {
            // Figure out if this person has logged in anonymously before
            String guestToken = preferences.getString("guestToken", null);
            if (guestToken != null) {
                // Returning guest, set preferences and go
                editor.putString("username", GUEST);
                editor.putString("token", guestToken);
                editor.apply();
                goToMain();
            }
            else {
                // Create a new user with an event list on the Firebase server with a unique id
                createGuest();
            }
        }
        else {
            // ToS not accepted
            askForToS();
        }
    }

    public void verifyUser(String email, String password) {
        String URL = String.format("%s/verify", MainActivity.FLASK);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("result").equals("success")) {
                        // This user information is valid
                        onLoginSuccessful(json.getString("username"));
                    }
                    else {
                        // Sucks to be you
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onLoginInvalid();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
            }
        });
    }

    // Successful login - set SharedPreferences and go to MainActivity
    public void onLoginSuccessful(String username) {
        editor.putString("username", username);
        editor.putString("token", username);
        editor.commit();
        goToMain();
    }

    // Terms of Service not accepted - fail user and try again
    public void askForToS() {
        // Re-enable everything
        unlockInterface();

        // Change the header to a warning
        header.setText("Please accept the Terms of Service.");
        header.setTextColor(Color.RED);

        // Shake and Bake
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        layout.startAnimation(shake);
    }

    // Failed login - warn user of failure
    public void onLoginInvalid() {
        // Re-enable everything
        unlockInterface();

        // Change the header to a warning
        header.setText("Login failed. Please try again.");
        header.setTextColor(Color.RED);

        // Shake and Bake
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        layout.startAnimation(shake);
    }

    // Send a request to server to create a new guest ID and get that ID back
    public void createGuest() {
        String URL = String.format("%s/newguest", MainActivity.FLASK);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("THE CALL", "IT FAILED");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    onCreateGuest(json.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
                }
            }
        });
    }

    // Use ID to start guest session
    public void onCreateGuest(String id) {
        editor.putString("username", GUEST);
        editor.putString("token", id);
        editor.putString("guestToken", id);
        editor.apply();
        goToMain();
    }

    // After all this crap, we're successful. Start the MainActivity
    public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

