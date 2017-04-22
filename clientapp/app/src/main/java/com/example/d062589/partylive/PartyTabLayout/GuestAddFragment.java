package com.example.d062589.partylive.PartyTabLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.R;
import com.example.d062589.partylive.Utils.MyListener;
import com.example.d062589.partylive.Utils.RestClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by D062589 on 18.04.2017.
 */

public class GuestAddFragment extends DialogFragment{

    private Party currentParty;
    private int position = -1;
    EditText guestMail;
    private Context context;
    private String userId;
    private String sessionCookie;


    public static GuestAddFragment newInstance(Party currentParty, int position, String userId, String sessionCookie) {
        GuestAddFragment guestAddFragment = new GuestAddFragment();
        guestAddFragment.currentParty = currentParty;
        guestAddFragment.position = position;
        guestAddFragment.userId = userId;
        guestAddFragment.sessionCookie = sessionCookie;
        return guestAddFragment;
    }

    public static GuestAddFragment newInstance(Party currentParty, String userId, String sessionCookie) {
        GuestAddFragment guestAddFragment = new GuestAddFragment();
        guestAddFragment.currentParty = currentParty;
        guestAddFragment.userId = userId;
        guestAddFragment.sessionCookie = sessionCookie;
        return guestAddFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_guest_add, null);

        guestMail = (EditText) view.findViewById(R.id.guest_mail);

        if (position != -1) {
            guestMail.setText(currentParty.getGuests().get(position).getUsername());
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!guestMail.getText().toString().equals("")) {

                            ProgressDialog progress = new ProgressDialog(context);
                            progress.setTitle("Searching for username");
                            progress.setMessage("Please Wait...");
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();

                            // Set timeout to progress
                            //timerDelayRemoveDialog

                            try {
                                requestGuest(progress);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mListener.onGuestDialogPositiveClick(null);
                        }


                    }
                })


                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GuestAddFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


    /**
     * Request guest name, image  by username
     * @param progress ProgressDialog for user acknowledge
     * @throws JSONException Error while building the request payload
     */
    private void requestGuest(final ProgressDialog progress) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("username", guestMail.getText().toString());

        RestClient.getInstance().post(userId, sessionCookie, payload, "/users/lookup", new MyListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {

                    if (response != null) {
                    // If there are no parties on the server, create new ones through post
                    Party.Guest guest = new Gson().fromJson(String.valueOf(response), Party.Guest.class);
                    guest.setUsername(guestMail.getText().toString());

                    if (position != -1) { // Edit BringItem
                        currentParty.getGuests().set(position, guest);
                    } else { // New BringItem
                        currentParty.addGuest(guest);
                    }
                    progress.dismiss();

                    mListener.onGuestDialogPositiveClick(currentParty);

                    } else {
                        progress.dismiss();
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }


    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface GuestDialogListener {
        public void onGuestDialogPositiveClick(Party currentParty);
    }

    // Use this instance of the interface to deliver action events
    GuestDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (GuestDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GuestDialogListener");
        }
    }
}
