package com.example.d062589.partylive.PartyTabLayout;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.RecyclerClickListener;
import com.example.d062589.partylive.Utils.RecyclerTouchListener;


/**
 * Created by D062589 on 21.03.2017.
 */

public class BringalongListFragment extends Fragment {

    Context context;
    Party party;
    BringAlongRecyclerViewAdapter mAdapter;
    public RecyclerView mRecyclerView;

    private RelativeLayout amount;
    private RelativeLayout apply;
    private RelativeLayout cancel;
    private RelativeLayout increase;
    private RelativeLayout prevAmount;
    private RelativeLayout prevApply;
    private RelativeLayout prevCancel;
    private RelativeLayout prevIncrease;


    // newInstance constructor for creating fragment with party
    public static BringalongListFragment newInstance(Party party) {
        BringalongListFragment bringalongFragment = new BringalongListFragment();
        bringalongFragment.party = party;
        return bringalongFragment;
    }

    // newInstance constructor for creating fragment without party
    public static BringalongListFragment newInstance() {
        BringalongListFragment bringalongFragment = new BringalongListFragment();
        return bringalongFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_bringalong, container, false);
        this.context = view.getContext();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.bringalongRecycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter
        mAdapter = new BringAlongRecyclerViewAdapter(party);
        mRecyclerView.setAdapter(mAdapter);

        // Add dividers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set box sizes
        float scale = context.getResources().getDisplayMetrics().density;
        final int amountNumberBoxSize = (int) (30 * scale + 0.5f);


        if (party != null) {
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, mRecyclerView, new RecyclerClickListener() {
                @Override
                public void onClick(View view, int position) {

                    if (!party.getBringitems().get(position).isLocked()) {

                        // Expand all interaction buttons
                        amount = (RelativeLayout) view.findViewById(R.id.amount);
                        ViewGroup.LayoutParams amountParams = amount.getLayoutParams();

                        cancel = (RelativeLayout) view.findViewById(R.id.cancelButton);
                        ViewGroup.LayoutParams cancelParams = cancel.getLayoutParams();

                        apply = (RelativeLayout) view.findViewById(R.id.applyButton);
                        ViewGroup.LayoutParams applyParams = apply.getLayoutParams();

                        increase = (RelativeLayout) view.findViewById(R.id.increaseButton);
                        ViewGroup.LayoutParams increaseParams = increase.getLayoutParams();

                        // Collapse old row if new one is selected
                        if (amount != prevAmount && prevAmount != null) {
                            BringAlongRecyclerViewAdapter.ViewHolder.hideElement(prevAmount);
                            BringAlongRecyclerViewAdapter.ViewHolder.hideElement(prevCancel);
                            BringAlongRecyclerViewAdapter.ViewHolder.hideElement(prevApply);
                            BringAlongRecyclerViewAdapter.ViewHolder.hideElement(prevIncrease);
                        }

                        // Expand new item
                        amountParams.width = amountNumberBoxSize;
                        cancelParams.width = amountNumberBoxSize;
                        applyParams.width = amountNumberBoxSize;
                        increaseParams.width = amountNumberBoxSize;

                        // Apply Params
                        amount.setLayoutParams(amountParams);
                        cancel.setLayoutParams(cancelParams);
                        apply.setLayoutParams(applyParams);
                        increase.setLayoutParams(increaseParams);

                        // Store variables for another usage
                        prevAmount = amount;
                        prevCancel = cancel;
                        prevApply = apply;
                        prevIncrease = increase;
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                    if (party.getBringitems().get(position).isLocked()) {
                        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(50);

                        TextView amountText = (TextView) view.findViewById(R.id.amountText);
                        amountText.setText("1x");
                        BringAlongRecyclerViewAdapter.ViewHolder.hideElement(amount);
                        amount.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

                        // TODO: new post request


                        party.getBringitems().get(position).setLocked(false);
                    }
                }
            }));
        }

        return view;
    }


    public void refreshBringItemRows(Party currentParty) {
        mAdapter.refreshBringItemRows(currentParty);
    }
}

