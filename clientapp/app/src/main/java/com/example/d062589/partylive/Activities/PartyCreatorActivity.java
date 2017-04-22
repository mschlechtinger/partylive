package com.example.d062589.partylive.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.Models.User;
import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.FontAwesome;
import com.example.d062589.partylive.Utils.MyListener;
import com.example.d062589.partylive.Utils.PrefUtils;
import com.example.d062589.partylive.Utils.RestClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by D062589 on 15.03.2017.
 */

public class PartyCreatorActivity extends AppCompatActivity {

    private PrefUtils prefUtils;
    private User user;
    private String sessionCookie;
    private String userId;
    private Location mLastKnownLocation;
    private Party party;
    private Context context;
    private String resizedImage;

    ImageView partyImg;
    FontAwesome editIcon;


    /**
     * Hide Navbar and Statusbar for Fullscreen Map
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_creator);

        context = getApplicationContext();

        // Get Intent information from Map Screen
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLastKnownLocation = extras.getParcelable("LOCATION");
        }

        partyImg = (ImageView) findViewById(R.id.party_image);
        editIcon = (FontAwesome) findViewById(R.id.edit_icon);

        if (party == null) {
            party = new Party();
        }

        prefUtils = PrefUtils.getInstance(getApplicationContext());
        user = prefUtils.getCurrentUser();
        sessionCookie = user.session;
        userId = user.userID;
    }


    public void loadImg(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }


    public String resizeBase64Image(String base64image) {
        int IMG_WIDTH = 150;
        int IMG_HEIGHT = 200;

        byte[] encodeByte = Base64.decode(base64image.getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);


        if (image.getHeight() <= 400 && image.getWidth() <= 400) {
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, IMG_WIDTH, IMG_HEIGHT, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);

    }

    /**
     * This code is being called after the image has been uploaded
     *
     * @param requestCode code
     * @param resultCode  successful/ failure
     * @param data        base64 data of the img
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                String resizedImage = resizeBase64Image(encodedImage);

                partyImg.setImageBitmap(bitmap);
                editIcon.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void configurePartyDetails(View view) {
        EditText partyName = (EditText) findViewById(R.id.party_name);
        if (!partyName.getText().toString().equals("")) {

            Intent intent = new Intent(this, PartyConfiguratorActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("LOCATION", mLastKnownLocation);

            party.setTitle(partyName.getText().toString());
            if (resizedImage != null) {
                party.setImgUrl(resizedImage);
            }
            String partyJson = new Gson().toJson(party);
            extras.putString("PARTY_JSON", partyJson);
            intent.putExtras(extras);
            startActivity(intent);
        } else {
            Toast.makeText(context, "Please fill out the Name.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Just for testing reasons to create parties if there are none
     * TODO: DELETE WHEN GOING PRODUCTIVE
     */
    public void createSamplePartyHere(View view) throws JSONException {

        JSONObject payload = new JSONObject();

        payload.put("title", "Sample Party");
        payload.put("description", getResources().getString(R.string.lorem_ipsum));
        payload.put("imgUrl", "http://2static.fjcdn.com/large/pictures/3a/34/3a3451_5582801.jpg");

        // Location
        JSONObject location = new JSONObject();
        location.put("latitude", mLastKnownLocation.getLatitude());
        location.put("longitude", mLastKnownLocation.getLongitude());
        payload.put("location", location);

        payload.put("public", false);

        // Guests
        JSONArray guests = new JSONArray();
        guests.put("58ca60f96c3e73484a856531");
        guests.put("58d2a938edc99b1dcc648206");
        payload.put("guests", guests);

        // StartDate
        SimpleDateFormat startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ms'Z'"); //2017-03-31T10:50:42.389Z
        Date date = new Date();
        System.out.println(startDate.format(date));
        payload.put("startDate", startDate.format(date));

        // BringItems
        JSONArray bringItems = new JSONArray();
        // BringItem 1
        JSONObject bringItem1 = new JSONObject();
        bringItem1.put("title", "Käse");
        bringItem1.put("quantity", 5);
        // BringItem 2
        JSONObject bringItem2 = new JSONObject();
        bringItem2.put("title", "Bier");
        bringItem2.put("quantity", 50);
        // Assignees
        //JSONArray assignees = new JSONArray();
        //assignees.put("58d2a938edc99b1dcc648206");
        //bringItem1.put("assignees", assignees);
        //bringItem2.put("assignees", assignees);
        bringItems.put(bringItem1);
        bringItems.put(bringItem2);
        payload.put("bringItems", bringItems);

        RestClient.getInstance().post(userId, sessionCookie, payload, "/events", new MyListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                if (response != null) {
                    Toast.makeText(context, "Sample Party created at your position!", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

}
