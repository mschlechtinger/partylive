package com.example.d062589.partylive.PartyTabLayout;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by D062589 on 21.03.2017.
 */

public class InfoListFragment extends Fragment {
    Party party;
    View view;

    // newInstance constructor for creating fragment with party
    public static InfoListFragment newInstance(Party party) {
        InfoListFragment infoFragment = new InfoListFragment();
        infoFragment.party = party;
        return infoFragment;
    }

    // newInstance constructor for creating fragment without party
    public static InfoListFragment newInstance() {
        InfoListFragment infoFragment = new InfoListFragment();
        return infoFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_info, container, false);
        refreshInfo();
        return view;
    }

    private void refreshInfo() {
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView location = (TextView) view.findViewById(R.id.location);
        if (party == null) {
            party = new Party();
        }
        description.setText(party.getDescription());

        if (party.getLocation() != null) {

            // Get Location Details
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(view.getContext(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(party.getLocation().getLatitude(),
                        party.getLocation().getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String postalCode = addresses.get(0).getPostalCode();
            location.setText(address + ", " + postalCode + ", " + city);
        }
    }


    public void addInfo(Party.PartyLocation loc, String desc) {
        party.setDescription(desc);
        party.setLocation(loc);
        refreshInfo();
    }
}

