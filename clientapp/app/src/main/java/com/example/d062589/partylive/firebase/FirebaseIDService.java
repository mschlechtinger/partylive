package com.example.d062589.partylive.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.d062589.partylive.Models.User;
import com.example.d062589.partylive.Utils.MyListener;
import com.example.d062589.partylive.Utils.PrefUtils;
import com.example.d062589.partylive.Utils.RestClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kauppfbi on 14.03.2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    private Context context;
    private PrefUtils prefUtils;

    @Override
    public void onTokenRefresh() {
        context = getApplicationContext();
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        SharedPreferences sharedPreferences = context.getSharedPreferences("token", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", refreshedToken);
        editor.commit();

        //check if device already exists
        prefUtils = PrefUtils.getInstance(context);
        User currentUser = prefUtils.getCurrentUser();
        if (currentUser != null) {
            sendRegistrationToServer(refreshedToken, currentUser.userID, currentUser.session);
        }
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token, final String userId, final String sessionCookie) {
        //send device token to backend

        new android.os.Handler().
                post(
                        new Runnable() {
                            public void run() {
                                JSONObject payload = new JSONObject();

                                try {
                                    payload.put("userId", userId);
                                    payload.put("session", sessionCookie);
                                    payload.put("deviceID", token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String path = "/auth/deviceIdChange";

                                RestClient.getInstance(context).post(payload, path, new MyListener<JSONObject>() {
                                    @Override
                                    public void getResult(JSONObject response) {
                                        if (response != null) {
                                            // Network & JSON Variables
                                            Log.e(TAG, "ServerResponse: " + response);
                                        } else {
                                            Log.e(TAG, "No response from server!");
                                        }
                                    }

                                });
                            }
                        });
    }
}
