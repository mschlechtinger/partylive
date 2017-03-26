package com.example.d062589.partylive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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

    private LinearLayout linearLayout;

    //regular Login / Sign Up
    private Button loginButton;
    private Button signuUpButton;

    //Facebook Login
    private CallbackManager callbackManager;
    private LoginButton loginButtonFacebook;
    private TextView btnLogin;

    private ProgressDialog progressDialog;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide();

        linearLayout = (LinearLayout) findViewById(R.id.background);
        Drawable background = linearLayout.getBackground();
        background.setAlpha(80);

        loginButton = (Button) findViewById(R.id.btn_login);
        signuUpButton = (Button) findViewById(R.id.btn_signup);

        if (PrefUtils.getCurrentUser(StartActivity.this) != null) {
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

        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
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
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                            try {
                                user = new User();
                                user.facebookID = object.getString("id").toString();
                                user.email = object.getString("email").toString();
                                user.name = object.getString("name").toString();
                                user.gender = object.getString("gender").toString();
                                PrefUtils.setCurrentUser(user, StartActivity.this);

                                //TODO: send data to backend

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(StartActivity.this, "welcome " + user.name, Toast.LENGTH_LONG).show();
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
        startActivity(intent);
    }

}
