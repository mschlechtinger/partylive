package com.example.d062589.partylive.Activities;

import android.app.ProgressDialog;
import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kauppfbi on 22.03.2017.
 */


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getSimpleName();

    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private Button signupButton;
    private TextView loginLink;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        context = getApplicationContext();

        emailText = (TextInputEditText) findViewById(R.id.input_email);
        passwordText = (TextInputEditText) findViewById(R.id.input_password);
        signupButton = (Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);

        signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    signup();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  Start the Login activity
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void signup() throws JSONException {
        Log.d(TAG, "Login");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering ...");
        progressDialog.show();

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        new android.os.Handler().
                post(
                        new Runnable() {
                            public void run() {
                                JSONObject payload = new JSONObject();

                                try {
                                    payload.put("username", email);
                                    payload.put("password", password);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String path = "/auth/register";

                                RestClient.getInstance(context).post(payload, path, new MyListener<JSONObject>() {
                                    @Override
                                    public void getResult(JSONObject response) {
                                        if (response != null) {
                                            Log.e(TAG, response.toString());
                                            onSignupSuccess(email, password);

                                        } else {
                                            onSignupFailed(progressDialog);
                                        }
                                    }

                                });
                            }
                        });

    }

    public void onSignupSuccess(String email, String password) {
        signupButton.setEnabled(true);
        Intent intent = getIntent();
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "SignUp failed! Try it again", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    private void onSignupFailed(ProgressDialog progressDialog) {
        Toast.makeText(getBaseContext(), "SignUp failed! Try it again", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        signupButton.setEnabled(true);
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
