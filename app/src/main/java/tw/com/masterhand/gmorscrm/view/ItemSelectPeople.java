package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.ContactPersonSelectActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.ParticipantSelectActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemSelectPeople extends RelativeLayout implements ItemSelectCustomer.CustomerSelectListener {
    @BindView(R.id.btnContacter)
    Button btnContacter;
    @BindView(R.id.btnParticipant)
    Button btnParticipant;

    Activity context;

    ItemSelectCustomer itemSelectCustomer;
    List<Contacter> contacters;
    List<Participant> participants;

    String contacterTitle;
    String participantTitle;

    public ItemSelectPeople(Context context) {
        super(context);
        init(context);
    }

    public ItemSelectPeople(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSelectPeople(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_select_people, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
        contacterTitle = context.getString(R.string.contact);
        participantTitle = context.getString(R.string.participant);
        contacters = new ArrayList<>();
        participants = new ArrayList<>();
    }

    private void updateCount() {
        StringBuilder contact = new StringBuilder(contacterTitle);
        int size = 0;
        for (Contacter item : contacters) {
            if (item.getDeleted_at() == null)
            {
                size++;
            }
        }
        if (size > 0)
            contact.append(" ").append(size);
        btnContacter.setText(contact.toString());
        size = 0;
        StringBuilder participant = new StringBuilder(participantTitle);
        for (Participant item : participants) {
            if (item.getDeleted_at() == null && !item.getUser_id().equals(TokenManager.getInstance().getUser().getId()))
            {
                size++;
            }
        }
        if (size > 0)
            participant.append(" ").append(size);
        btnParticipant.setText(participant.toString());
    }

    public void setItemSelectCustomer(ItemSelectCustomer customer) {
        itemSelectCustomer = customer;
        itemSelectCustomer.addCustomerSelectedListener(this);
    }

    @OnClick(R.id.btnContacter)
    void selectContacter() {
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(context, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(context, ContactPersonSelectActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, itemSelectCustomer.getCustomer().getId());
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING).create();
        intent.putExtra(MyApplication.INTENT_KEY_PEOPLE, gson.toJson(contacters));
        context.startActivityForResult(intent, MyApplication.REQUEST_SELECT_CONTACTER);
    }

    @OnClick(R.id.btnParticipant)
    void selectParticipant() {
        Intent intent = new Intent(context, ParticipantSelectActivity.class);
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING).create();
        intent.putExtra(MyApplication.INTENT_KEY_PEOPLE, gson.toJson(participants));
        context.startActivityForResult(intent, MyApplication.REQUEST_SELECT_PARTICIPANT);
    }

    public void addParticipant(Participant participant) {
        boolean isContain = false;
        for (Participant user : participants) {
            if (user.getId().equals(participant.getId()))
                isContain = true;
        }
        if (!isContain) {
            participants.add(participant);
            updateCount();
        }
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void addContacter(Contacter contactPerson) {
        boolean isContain = false;
        for (Contacter contacter : contacters) {
            if (contacter.getId().equals(contactPerson.getId()))
                isContain = true;
        }
        if (!isContain) {
            contacters.add(contactPerson);
            updateCount();
        }
    }

    public void setParticipantTitle(String title) {
        participantTitle = title;
        updateCount();
    }

    public void setParticipants(List<Participant> input) {
        participants = new ArrayList<>();
        for (Participant participant : input) {
            participants.add(participant);
        }
        updateCount();
    }

    public void setContacters(List<Contacter> input) {
        contacters = new ArrayList<>();
        for (Contacter contacter : input) {
            contacters.add(contacter);
        }
        updateCount();
    }

    public List<Contacter> getContacters() {
        return contacters;
    }

    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING).create();
            switch (requestCode) {
                case MyApplication.REQUEST_SELECT_CONTACTER:
                    contacters = gson.fromJson(data.getStringExtra(MyApplication.INTENT_KEY_PEOPLE), new TypeToken<ArrayList<Contacter>>() {
                    }.getType());
                    break;
                case MyApplication.REQUEST_SELECT_PARTICIPANT:
                    participants = gson.fromJson(data.getStringExtra(MyApplication.INTENT_KEY_PEOPLE), new TypeToken<ArrayList<Participant>>() {
                    }.getType());
                    break;
            }
            updateCount();
        }
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        resetContacters();
    }

    private void resetContacters() {
        contacters.clear();
        updateCount();
    }

    public void disableContacter() {
        btnContacter.setVisibility(GONE);
    }

    public void disableParticipant() {
        btnParticipant.setVisibility(GONE);
    }
}
