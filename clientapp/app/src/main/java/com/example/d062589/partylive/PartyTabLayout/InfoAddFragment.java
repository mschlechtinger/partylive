package com.example.d062589.partylive.PartyTabLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.text.MessagePattern;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.FontAwesome;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by D062589 on 18.04.2017.
 */

public class InfoAddFragment extends DialogFragment{


    Party.PartyLocation location;
    EditText locationInput;
    Party currentParty;

    public static InfoAddFragment newInstance(Party.PartyLocation loc, Party currentParty) {
        InfoAddFragment infoAddFragment = new InfoAddFragment();
        infoAddFragment.location = loc;
        infoAddFragment.currentParty = currentParty;
        return infoAddFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_info_add, null);

        locationInput = (EditText) view.findViewById(R.id.location);
        final EditText description = (EditText) view.findViewById(R.id.description);
        final TextView locationCross = (TextView) view.findViewById(R.id.locationCross);

        if (currentParty.getDescription() != null) {
            description.setText(currentParty.getDescription());
        }

        locationCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation(v);
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
            // Add action buttons
            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    mListener.onInfoDialogPositiveClick(description.getText().toString(),
                            location,
                            locationInput.getText().toString());
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    InfoAddFragment.this.getDialog().cancel();
                }
            });
        return builder.create();
    }


    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface InfoDialogListener {
        public void onInfoDialogPositiveClick(String description, Party.PartyLocation location, String locationText);
    }

    // Use this instance of the interface to deliver action events
    InfoDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (InfoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement InfoDialogListener");
        }
    }

    public void getCurrentLocation(View view) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(view.getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();

        locationInput.setText(address + ", " + city);
    }
}
