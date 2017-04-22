package com.example.d062589.partylive.PartyTabLayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D062589 on 21.03.2017.
 */

public class GuestListFragment extends Fragment {
    Party party;
    Context context;
    public RecyclerView mRecyclerView;

    GuestListRecyclerViewAdapter mAdapter;

    // newInstance constructor for creating fragment with party
    public static GuestListFragment newInstance(Party party) {
        GuestListFragment guestFragment = new GuestListFragment();
        guestFragment.party = party;
        return guestFragment;
    }

    // newInstance constructor for creating fragment without party
    public static GuestListFragment newInstance() {
        GuestListFragment guestFragment = new GuestListFragment();
        return guestFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_guest, container, false);
        this.context = view.getContext();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.guestListRecycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter
        mAdapter = new GuestListRecyclerViewAdapter(party);
        mRecyclerView.setAdapter(mAdapter);

        // Add dividers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    public void refreshGuestRows(Party currentParty) {
        mAdapter.refreshGuestRows(currentParty);
    }
}

