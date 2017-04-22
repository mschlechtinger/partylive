package com.example.d062589.partylive.PartyTabLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.d062589.partylive.Models.Party;
import com.example.d062589.partylive.R;

/**
 * Created by D062589 on 18.04.2017.
 */

public class BringalongAddFragment extends DialogFragment{

    private Party currentParty;
    private int position = -1;

    public static BringalongAddFragment newInstance(Party currentParty, int position) {
        BringalongAddFragment bringalongAddFragment = new BringalongAddFragment();
        bringalongAddFragment.currentParty = currentParty;
        bringalongAddFragment.position = position;
        return bringalongAddFragment;
    }

    public static BringalongAddFragment newInstance(Party currentParty) {
        BringalongAddFragment bringalongAddFragment = new BringalongAddFragment();
        bringalongAddFragment.currentParty = currentParty;
        return bringalongAddFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_bringalong_add, null);

        final EditText item = (EditText) view.findViewById(R.id.item);
        final EditText amount = (EditText) view.findViewById(R.id.amount);

        if (position != -1) {
            item.setText(currentParty.getBringitems().get(position).getTitle());
            amount.setText(Integer.toString(currentParty.getBringitems().get(position).getQuantity()));
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!amount.getText().toString().equals("")
                                && !item.getText().toString().equals("")) {
                            Party.BringItem bringitem = new Party.BringItem(item.getText().toString(),
                                    Integer.parseInt(amount.getText().toString()),
                                    Integer.parseInt(amount.getText().toString()));

                            if (position != -1) { // Edit BringItem
                                currentParty.getBringitems().set(position, bringitem);
                            } else { // New BringItem
                                currentParty.addBringItem(bringitem);
                            }
                            mListener.onBringalongDialogPositiveClick(currentParty);
                        } else {
                            mListener.onBringalongDialogPositiveClick(null);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BringalongAddFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }


    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface BringalongDialogListener {
        public void onBringalongDialogPositiveClick(Party currentParty);
    }

    // Use this instance of the interface to deliver action events
    BringalongDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (BringalongDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement BringalongDialogListener");
        }
    }
}
