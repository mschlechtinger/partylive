package com.example.d062589.partylive.PartyTabLayout;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.FontAwesome;

/**
 * Created by D062589 on 06.04.2017.
 */

public class GuestListRecyclerViewAdapter extends RecyclerView.Adapter<GuestListRecyclerViewAdapter.ViewHolder> {
    private Party party;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Party.Guest guest;
        public TextView name;
        public FontAwesome relation;
        public ImageView img;

        Context context;
        int remainingAmount;


        /**
         * provides a constructor for the viewholder, initializing all used elements
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            name = (TextView) view.findViewById(R.id.guest_name);
            relation = (FontAwesome) view.findViewById(R.id.guest_relation);
            img = (ImageView) view.findViewById(R.id.guest_img);
        }

    }

    /**
     * Provide a suitable constructor
     * @param party party object used for binding
     */
    public GuestListRecyclerViewAdapter(Party party) {
        if (party != null) {
            this.party = party;
        } else
            this.party = new Party();
    }


    public void refreshGuestRows(Party currentParty) {
        this.party.addGuests(currentParty.getGuests());
        notifyDataSetChanged();
    }


    /**
     * Create new views (invoked by the layout manager)
     * @param parent ViewPager reference
     * @param viewType reference to the row view
     * @return
     */
    @Override
    public GuestListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new textview
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_guest_row, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder Viewholder
     * @param position row position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Party.Guest guest = party.getGuests().get(position);
        holder.guest = guest;

        holder.name.setText(guest.getName());

        if (guest.getRelation() != null) {
            switch (guest.getRelation()) {
                case "Friend": holder.relation.setText("\uF0C0 Friend");break;
                case "Close Friend": holder.relation.setText("\uF004 Close Friend");break;
                case "No Friend": holder.relation.setText(guest.getMutualFriendsAmount() +" Mutual Friends");break;
                default: break;
            }
        } else {
            holder.relation.setText("0 Mutual Friends");
        }


        Glide.with(holder.img.getContext())
                .load(guest.getImgUrl())
                .placeholder(R.drawable.user_placeholder)
                .crossFade()
                .into(holder.img);
    }


    /**
     * Return the size of the Bringitems (invoked by the layout manager)
     * @return length of the bringItems Array
     */
    @Override
    public int getItemCount() {
        if (party != null)
            return party.getGuests().size();
        else {
            return 0;
        }
    }
}
