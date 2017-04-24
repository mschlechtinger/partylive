package com.example.d062589.partylive.Models;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.d062589.partylive.R;

import java.util.ArrayList;

/**
 * Created by D062589 on 10.03.2017.
 */

public class Party extends BaseObservable {
    private String _id;
    private boolean publicParty;
    private String title;
    private String imgUrl;
    private String[] guestImgs;
    private int guestCount;
    private Organizer organizer;
    private PartyLocation location;
    private String description;
    private ArrayList<BringItem> bringItems = new ArrayList<>();
    private ArrayList<Guest> guests = new ArrayList<>();
    private int participationStatus;

    public String getTitle() {
        return title;
    }

    public String get_id() {
        return _id;
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
        Glide.with(view.getContext())
                .load(imageUri)
                .placeholder(R.drawable.image_placeholder)
                .crossFade()
                .into(view);
    }


    public int getGuestCount() {
        return guestCount;
    }

    public String[] getGuestImgs() {
        return guestImgs;
    }

    public boolean isPublicParty() {
        return publicParty;
    }

    public String getDescription() {
        return description;
    }



    /**
     * Subclass Organizer
     */
    public Organizer getOrganizer() {
        return organizer;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getParticipationStatus() {
        return participationStatus;
    }

    public void setParticipationStatus(int participationStatus) {
        this.participationStatus = participationStatus;
    }


    public class Organizer extends Person {
        public Organizer(String name, String imgUrl, String username) {
            super(name, imgUrl, username);
        }
    }


    /**
     * Subclass Location
     */
    public PartyLocation getLocation() {
        return location;
    }

    public void setLocation(PartyLocation location) {
        this.location = location;
    }

    public static class PartyLocation {
        private double latitude;
        private double longitude;

        public PartyLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }


    /**
     * Subclass Bringitem
     */
    public ArrayList<BringItem> getBringitems() {
        return bringItems;
    }

    public void addBringItem(BringItem bringitem) {
        bringItems.add(bringitem);
    }

    public void addBringItems(ArrayList<BringItem> bringitems) {
        this.bringItems = bringitems;
    }

    public static class BringItem {
        private int remaining;
        private String title;
        private int quantity;
        private String _id;
        private ArrayList<Assignee> assignees = new ArrayList<>();
        private boolean locked;

        public BringItem(String title, int quantity, int remaining) {
            this.title = title;
            this.quantity = quantity;
            this.remaining = remaining;
        }

        public int getRemaining() {
            return remaining;
        }

        public String getTitle() {
            return title;
        }

        public int getQuantity() {
            return quantity;
        }

        public String get_id() {
            return _id;
        }


        public void setRemaining(int remaining) {
            this.remaining = remaining;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public ArrayList<Assignee> getAssignees() {
            return assignees;
        }

        public void addAssignee(Assignee assignee) {
            assignees.add(assignee);
        }

        public void addAssignees(ArrayList<Assignee> assignees) {
            this.assignees = assignees;
        }


        /**
         * SubSubClass Assignee
         */
        public static class Assignee {
            private String guestId;
            private int amount;

            public String getGuestId() {
                return guestId;
            }

            public void setGuestId(String guestId) {
                this.guestId = guestId;
            }

            public int getAmount() {
                return amount;
            }

            public void setAmount(int amount) {
                this.amount = amount;
            }
        }

    }

    /**
     * Subclass Guest
     */
    public ArrayList<Guest> getGuests() {
        return guests;
    }

    public void addGuest(Party.Guest guest) {
        this.guests.add(guest);
    }

    public void addGuests(ArrayList<Guest> guests) {
        this.guests = guests;
    }

    public static class Guest extends Person{
        private String relation;
        private int mutualFriendsAmount;

        public Guest(String relation, int mutualFriendsAmount, String name, String imgUrl, String username) {
            super(name, imgUrl, username);
            this.relation = relation;
            this.mutualFriendsAmount = mutualFriendsAmount;
        }

        public String getRelation() {
            return relation;
        }

        public int getMutualFriendsAmount() {
            return mutualFriendsAmount;
        }
    }
}

