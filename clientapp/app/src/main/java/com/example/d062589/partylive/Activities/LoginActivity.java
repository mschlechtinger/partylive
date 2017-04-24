package com.example.d062589.partylive.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d062589.partylive.Models.User;
import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.MyListener;
import com.example.d062589.partylive.Utils.PrefUtils;
import com.example.d062589.partylive.Utils.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KAUPPFBI on 12.01.2017.
 */

public class LoginActivity extends AppCompatActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;

    private Context context;
    private PrefUtils prefUtils;

    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private Button loginButton;
    private TextView signupLink;


    //set dev==true to use static login credentials
    //set dev==false to use text from input fields
    private boolean dev = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        context = getApplicationContext();

        emailText = (TextInputEditText) findViewById(R.id.input_email);
        passwordText = (TextInputEditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    login();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  Start the Signup activity
                Intent intent = new Intent(context, SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() throws JSONException {
        Log.d(TAG, "Login");

        /*if(!validate()){
            onLoginFailed();
            return;
        }*/

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email;
        final String password;
        if (dev == true) {
            email = "robefrt.schulzt@wovw.de";
            password = "ayylmao";
        } else {
            email = emailText.getText().toString();
            password = passwordText.getText().toString();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        final String deviceId = sharedPreferences.getString("token", null);

        new android.os.Handler().
                post(
                        new Runnable() {
                            public void run() {
                                JSONObject payload = new JSONObject();

                                try {
                                    payload.put("username", email);
                                    payload.put("password", password);
                                    payload.put("deviceId", deviceId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String path = "/auth/login";

                                RestClient.getInstance(context).post(payload, path, new MyListener<JSONObject>() {
                                    @Override
                                    public void getResult(JSONObject response) {
                                        if (response != null) {
                                            // Network & JSON Variables
                                            ObjectMapper mapper = new ObjectMapper();
                                            String session;
                                            String userId;

                                            // Get the session cookie & userId
                                            try {
                                                JsonNode root = mapper.readTree(response.toString());
                                                userId = root.get("userId").toString();
                                                session = root.at("/headers").get("set-cookie").toString();

                                                // Remove Quotes
                                                userId = userId.substring(1, userId.length() - 1);
                                                session = session.substring(1, session.length() - 1);

                                                Log.e(TAG, "userID: " + userId);
                                                Log.e(TAG, "session: " + session);

                                                onLoginSuccess(userId, session, progressDialog);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            onLoginFailed(progressDialog);
                                        }
                                    }

                                });
                            }
                        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                final String email = data.getStringExtra("email");
                final String password = data.getStringExtra("password");
                try {
                    login(email, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void login(final String email, final String password) throws JSONException {
        Log.d(TAG, "Login");
        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        SharedPreferences sharedPreferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        final String deviceId = sharedPreferences.getString("token", null);

        new android.os.Handler().
                post(
                        new Runnable() {
                            public void run() {
                                JSONObject payload = new JSONObject();

                                try {
                                    payload.put("username", email);
                                    payload.put("password", password);
                                    payload.put("deviceId", deviceId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String path = "/auth/login";

                                RestClient.getInstance(context).post(payload, path, new MyListener<JSONObject>() {
                                    @Override
                                    public void getResult(JSONObject response) {
                                        if (response != null) {
                                            // Network & JSON Variables
                                            ObjectMapper mapper = new ObjectMapper();
                                            String session;
                                            String userId;

                                            // Get the session cookie & userId
                                            try {
                                                JsonNode root = mapper.readTree(response.toString());
                                                userId = root.get("userId").toString();
                                                session = root.at("/headers").get("set-cookie").toString();

                                                // Remove Quotes
                                                userId = userId.substring(1, userId.length() - 1);
                                                session = session.substring(1, session.length() - 1);

                                                Log.e(TAG, "userID: " + userId);
                                                Log.e(TAG, "session: " + session);

                                                onLoginSuccess(userId, session, progressDialog);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            onLoginFailed(progressDialog);
                                        }
                                    }

                                });
                            }
                        });

    }

    public void onLoginSuccess(String userId, String session, ProgressDialog progressDialog) {
        loginButton.setEnabled(true);

        SharedPreferences sharedPreferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        String deviceId = sharedPreferences.getString("token", null);

        prefUtils = PrefUtils.getInstance(context);
        User user = new User();
        user.userID = userId;
        user.session = session;
        user.deviceID = deviceId;
        prefUtils.setCurrentUser(user);

        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        this.finish();
    }

    public void onLoginFailed(ProgressDialog progressDialog) {
        Toast.makeText(getBaseContext(), "Login failed! Try it again", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        loginButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }
        return valid;
    }
}