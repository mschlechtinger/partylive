package com.example.d062589.partylive;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

/**
 * Created by D062589 on 10.03.2017.
 */

public class Party extends BaseObservable {
    private int id;
    private double latitude;
    private double longitude;
    private boolean publicParty;
    private String title;
    private String imgUrl;
    private String[] guestImgs;
    private int numberOfGuests;
    private Organizer organizer;

    public String getTitle() {
        return title;
    }

    public boolean isPublic() {
        return publicParty;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPublicParty(boolean publicParty) {
        this.publicParty = publicParty;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getImgUrl() {
        return imgUrl;
    }


    // Binding for ImageViews
    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, String imageUri) {
        //view.setImageURI(Uri.parse(imageUri));
        // TODO: change to use backend information
        String resource = "android.resource://com.example.d062589.partylive/drawable/" + imageUri;
        /*Picasso.with(view.getContext())
                .load(resource)
                .placeholder(R.drawable.default_party)
                .into(view);*/

        Glide.with(view.getContext())
                .load(resource)
                .thumbnail(Glide.with(view.getContext())
                        .load(R.drawable.loading_spinner)) //load gif as thumbnail
                .placeholder(R.drawable.image_placeholder)
                .crossFade()
                .into(view);
    }


    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public String[] getGuestImgs() {
        return guestImgs;
    }



    // Subclass Organizer
    public Organizer getOrganizer() {
        return organizer;
    }

    public class Organizer {
        private String name;
        private String imgUrl;

        public String getName() {
            return name;
        }

        public String getImgUrl() {
            return imgUrl;
        }
    }
}

