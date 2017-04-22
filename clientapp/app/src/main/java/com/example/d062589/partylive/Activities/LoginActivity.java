package com.example.d062589.partylive.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.MyListener;
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

    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private Button loginButton;
    private TextView signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

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
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
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

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        new android.os.Handler().
                post(
                        new Runnable() {
                            public void run() {
                                JSONObject payload = new JSONObject();

                                try {
                                    payload.put("username", "robefrt.schulzt@wovw.de");
                                    payload.put("password", "ayylmao");

                                    //payload.put("username", email);
                                    //payload.put("password", password);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String path = "/auth/login";

                                RestClient.getInstance(getApplicationContext()).post(payload, path, new MyListener<JSONObject>() {
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

                                                Log.e(TAG, "userID: " + userId);
                                                Log.e(TAG, "session: " + session);

                                                onLoginSuccess(userId, session, progressDialog);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            onLoginFailed();
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

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String userId, String session, ProgressDialog progressDialog) {
        loginButton.setEnabled(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("session", session);
        startActivity(intent);
        progressDialog.dismiss();
        this.finish();
    }

    public void onLoginFailed() {
        //TODO: Implement logic for failed login here
        Toast.makeText(getBaseContext(), "Login failed! Try it again", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
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