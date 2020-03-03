package tw.com.masterhand.gmorscrm;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.model.ParticipantWithUser;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemUserList;

public class UserListActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    List<ParticipantWithUser> participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        participants = new ArrayList<>();
        String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
        String projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PARENT);
        if (!TextUtils.isEmpty(customerId)) {
            appbar.setTitle(getString(R.string.title_activity_user_list));
            mDisposable.add(DatabaseHelper.getInstance(this).getContactPersonByCustomer
                    (customerId).toList()
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ContactPerson>>() {

                        @Override
                        public void accept(List<ContactPerson> contactPeople) throws Exception {
                            updateContactPersonList(contactPeople);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(UserListActivity.this,
                                    throwable);
                        }
                    }));
        } else if (!TextUtils.isEmpty(projectId)) {
            appbar.setTitle(getString(R.string.participant));
            mDisposable.add(DatabaseHelper.getInstance(this).getParticipantByParent(projectId)
                    .observeOn
                            (AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<ParticipantWithUser>>() {

                        @Override
                        public void accept(List<ParticipantWithUser> participantWithUsers) throws
                                Exception {
                            participants = participantWithUsers;
                            updateList();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(UserListActivity.this,
                                    throwable);
                        }
                    }));
        } else {
            finish();
        }
    }

    void updateList() {
        container.removeAllViews();
        if (participants != null && participants.size() > 0) {
            for (ParticipantWithUser participant : participants) {
                if (participant.getUser() != null) {
                    ItemUserList item = new ItemUserList(this);
                    item.setUser(participant.getUser());
                    container.addView(item);
                }
            }
        } else {
            container.addView(getEmptyImageView(null));
        }
    }

    void updateContactPersonList(List<ContactPerson> list) {
        container.removeAllViews();
        if (list != null && list.size() > 0) {
            for (ContactPerson contactPerson : list) {
                if (contactPerson != null) {
                    ItemUserList item = new ItemUserList(this);
                    item.setContactPerson(contactPerson);
                    container.addView(item);
                }
            }
        } else {
            container.addView(getEmptyImageView(null));
        }
    }
}
