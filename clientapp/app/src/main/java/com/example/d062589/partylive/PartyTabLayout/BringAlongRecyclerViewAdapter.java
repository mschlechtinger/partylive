package com.example.d062589.partylive.PartyTabLayout;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.R;

/**
 * Created by D062589 on 06.04.2017.
 */

public class BringAlongRecyclerViewAdapter extends RecyclerView.Adapter<BringAlongRecyclerViewAdapter.ViewHolder> {
    private Party party;

    /**
     * Adds a new row to the bringitems programmatically
     * @param currentParty created bringitem
     */
    public void refreshBringItemRows(Party currentParty) {
        this.party.addBringItems(currentParty.getBringitems());
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public Party.BringItem bringItem;

        private TextView title;
        private TextView remaining;
        private RelativeLayout cancel;
        private RelativeLayout apply;
        private RelativeLayout amount;
        private RelativeLayout increase;
        private TextView amountText;

        Context context;
        int amountNumberBoxSize;
        int remainingAmount;


        /**
         * provides a constructor for the viewholder, initializing all used elements
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            context = view.getContext();


            title = (TextView) view.findViewById(R.id.title);
            remaining = (TextView) view.findViewById(R.id.needed);

            amount = (RelativeLayout) view.findViewById(R.id.amount);
            amountText = (TextView) view.findViewById(R.id.amountText);
            increase = (RelativeLayout) view.findViewById(R.id.increaseButton);
            cancel = (RelativeLayout) view.findViewById(R.id.cancelButton);
            apply = (RelativeLayout) view.findViewById(R.id.applyButton);

            // Add OnClickListener
            cancel.setOnClickListener(this);
            apply.setOnClickListener(this);
            increase.setOnClickListener(this);


            float scale = context.getResources().getDisplayMetrics().density;
            amountNumberBoxSize = (int) (30 * scale + 0.5f);
        }

        /**
         * Implements onClick methods for cancel, apply and increase button
         * @param view RecyclerView Row
         */
        @Override
        public void onClick(View view) {

            if (view.getId() == cancel.getId()){
                /**
                 * Click on Cancel Button within row
                 */
                // Hide old amount
                hideElement(amount);

                // Hide elements
                hideElement(apply);
                hideElement(cancel);
                hideElement(increase);

                amountText.setText(0 + "x");

            }
            if (view.getId() == increase.getId()) {
                /**
                 * Click on Increase Button within row
                 */
                int counter = Integer.parseInt(amountText.getText().toString().substring(0, amountText.getText().length() - 1));
                counter++;
                if (counter > remainingAmount) {
                    counter = remainingAmount;
                }
                amountText.setText(counter + "x");

            }
            if (view.getId() == apply.getId()) {
                /**
                 * Click on Apply Button within row
                 */
                // save and post to server
                amount.setBackgroundColor(ContextCompat.getColor(context, R.color.colorApply));

                // persist amount
                int counter = Integer.parseInt(amountText.getText().toString().substring(0, amountText.getText().length() - 1));
                bringItem.setRemaining(bringItem.getRemaining() - counter);
                persistItem();
                bringItem.setLocked(true);

                // Hide elements
                hideElement(apply);
                hideElement(cancel);
                hideElement(increase);
            }

    }

        /**
         * Hide element by reducing its width to zero
         * @param element a relativeLayout element
         */
        public static void hideElement(RelativeLayout element) {
            ViewGroup.LayoutParams layoutParams = element.getLayoutParams();
            layoutParams.width = 0;
            element.setLayoutParams(layoutParams);
        }


        /**
         * Send modified data to server
         */
        private void persistItem() {
            // TODO: Do Patch Request with bringitem information
            Toast.makeText(context, "Item persistet locally", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Provide a suitable constructor
     * @param party party object used for binding
     */
    public BringAlongRecyclerViewAdapter(Party party) {
        if (party != null) {
            this.party = party;
        } else
            this.party = new Party();
    }


    /**
     * Create new views (invoked by the layout manager)
     * @param parent ViewPager reference
     * @param viewType reference to the row view
     * @return
     */
    @Override
    public BringAlongRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // create a new textview
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_bringalong_row, parent, false);

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
        Party.BringItem bringItem = party.getBringitems().get(position);
        holder.bringItem = bringItem;

        holder.title.setText(bringItem.getTitle());

        int remainingAmount = bringItem.getRemaining();
        holder.remainingAmount = remainingAmount;
        holder.remaining.setText(remainingAmount + " needed");
    }


    /**
     * Return the size of the Bringitems (invoked by the layout manager)
     * @return length of the bringItems Array
     */
    @Override
    public int getItemCount() {
        if (party != null) {
            return party.getBringitems().size();
        }
        else {
            return 0;
        }
    }
}
