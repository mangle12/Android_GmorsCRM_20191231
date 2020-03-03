package tw.com.masterhand.gmorscrm.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.ContactCard;

public class ContactFragment extends DialogFragment {
    String TAG = getClass().getSimpleName();
    ContactCard contactCard;

    public static ContactFragment newInstance(ContactPerson contact) {
        ContactFragment f = new ContactFragment();
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING)
                .create();
        Bundle args = new Bundle();
        args.putString("contact", gson.toJson(contact));
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        contactCard = new ContactCard(getActivity());
        contactCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_white_corner));
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING).create();
        contactCard.setContact(gson.fromJson(getArguments().getString("contact"), ContactPerson.class));
        contactCard.showDetail();
        contactCard.setListener(new ContactCard.Listener() {
            @Override
            public void onEdit() {
                Dialog dialog = getDialog();
                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.e(TAG, "onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), MyApplication.DIALOG_STYLE);
        builder.setView(contactCard);
        return builder.create();
    }

}
