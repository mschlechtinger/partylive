package com.example.d062589.partylive.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.d062589.partylive.Models.User;
import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.MyListener;
import com.example.d062589.partylive.Utils.PrefUtils;
import com.example.d062589.partylive.Utils.RestClient;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kauppfbi on 23.03.2017.
 */

//TODO: automatic change of background image
//TODO: implementation of google+ login
//TODO: Single Sign On Behaviour of regular login
//TODO: layout.xml
public class StartActivity extends AppCompatActivity {

    private final static String TAG = StartActivity.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;

    private Context context;
    private PrefUtils prefUtils;

    private LinearLayout linearLayout;

    //regular Login / Sign Up
    private Button loginButton;
    private Button signuUpButton;

    //Facebook Login
    private CallbackManager callbackManager;
    private LoginButton loginButtonFacebook;
    private Button btnLoginFacebook;

    //Google+ Login
    private Button btnLoginGoogle;

    private ProgressDialog progressDialog;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        this.context = getApplicationContext();

        getSupportActionBar().hide();

        linearLayout = (LinearLayout) findViewById(R.id.background);
        Drawable background = linearLayout.getBackground();
        background.setAlpha(80);

        loginButton = (Button) findViewById(R.id.btn_login);
        signuUpButton = (Button) findViewById(R.id.btn_signup);

        btnLoginGoogle = (Button) findViewById(R.id.btnLoginGoogle);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "not yet implemented!", Toast.LENGTH_LONG).show();
            }
        });

        user = PrefUtils.getInstance(StartActivity.this).getCurrentUser();
        if (user != null) {
            Intent homeIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        callbackManager = CallbackManager.Factory.create();

        loginButtonFacebook = (LoginButton) findViewById(R.id.btn_login_facebook);

        loginButtonFacebook.setReadPermissions("public_profile", "email", "user_friends");

        btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(StartActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                loginButtonFacebook.performClick();
                loginButtonFacebook.setPressed(true);
                loginButtonFacebook.invalidate();
                loginButtonFacebook.registerCallback(callbackManager, mCallBack);
                loginButtonFacebook.setPressed(false);
                loginButtonFacebook.invalidate();

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
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void login(final String email, final String password) throws JSONException {
        Log.d(TAG, "Login");
        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(StartActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

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


        prefUtils = PrefUtils.getInstance(context);
        User user = new User();
        user.userID = userId;
        user.session = session;
        prefUtils.setCurrentUser(user);

        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        this.finish();
    }

    public void onLoginFailed(ProgressDialog progressDialog) {
        Toast.makeText(getBaseContext(), "Login failed! Try it again", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            progressDialog.dismiss();

            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            Log.e("response: ", response + "");
                            Log.e("data", object.toString());
                            try {
                                /*user = new User();
                                user.facebookID = object.getString("id").toString();
                                String email = object.getString("email").toString();
                                String name = object.getString("name").toString();
                                String gender = object.getString("gender").toString();*/

                                //PrefUtils.setCurrentUser(user, StartActivity.this);

                                //TODO: send data to backend
                                final JSONObject data = object;
                                //sendFacebookUserData(data);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(StartActivity.this, "welcome " + user.name, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            progressDialog.dismiss();
        }

        @Override
        public void onError(FacebookException e) {
            progressDialog.dismiss();
        }
    };

    public void onLoginPressed(View view) {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void onSignUpPressed(View view) {
        Intent intent = new Intent(StartActivity.this, SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

    private void sendFacebookUserData(final JSONObject response) {

        //Send data to backend and update Person object (set ID)
        //onSuccessfull Signin
//        new android.os.Handler().
//                post(
//                        new Runnable() {
//                            public void run() {
//                                JSONObject payload = new JSONObject();
//
//                                try {
//                                    payload.put("username", response.getString("email").toString());
//                                    //payload.put("password", "ayylmao");
//                                    payload.put("facebook-data", response);
//                                    //payload.put("username", email);
//                                    //payload.put("password", password);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                                String path = "/auth/login";
//
//                                RestClient.getInstance(getApplicationContext()).post(payload, path, new MyListener<JSONObject>() {
//                                    @Override
//                                    public void getResult(JSONObject response) {
//                                        if (response != null) {
//                                            // Network & JSON Variables
//                                            ObjectMapper mapper = new ObjectMapper();
//                                            String session;
//                                            String userId;
//
//                                            // Get the session cookie & userId
//                                            try {
//                                                JsonNode root = mapper.readTree(response.toString());
//                                                userId = root.get("userId").toString();
//                                                session = root.at("/headers").get("set-cookie").toString();
//
//                                                Log.e(TAG, "userID: " + userId);
//                                                Log.e(TAG, "session: " + session);
//
//                                                //onLoginSuccess(userId, session, progressDialog);
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        } else {
//                                            //onLoginFailed();
//                                        }
//                                    }
//
//                                });
//                            }
//                        });
    }

}
