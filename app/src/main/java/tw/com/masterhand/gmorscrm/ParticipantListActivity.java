package tw.com.masterhand.gmorscrm;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.model.ParticipantWithUser;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemParticipantList;

public class ParticipantListActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.containerAcceptYes)
    LinearLayout containerAcceptYes;
    @BindView(R.id.containerAcceptMaybe)
    LinearLayout containerAcceptMaybe;
    @BindView(R.id.containerAcceptNo)
    LinearLayout containerAcceptNo;
    @BindView(R.id.containerAcceptNone)
    LinearLayout containerAcceptNone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_list);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        String parentId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PARENT);
        if (TextUtils.isEmpty(parentId))
            finish();
        containerAcceptYes.removeAllViews();
        containerAcceptMaybe.removeAllViews();
        containerAcceptNo.removeAllViews();
        containerAcceptNone.removeAllViews();
        mDisposable.add(DatabaseHelper.getInstance(this).getParticipantByParent(parentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ParticipantWithUser>() {
                    @Override
                    public void accept(ParticipantWithUser participantWithUser) throws Exception {
                        if (!participantWithUser.getUser().getId().equals(TokenManager
                                .getInstance().getUser().getId())) {
                            LinearLayout container;
                            switch (participantWithUser.getParticipant().getAccept()) {
                                case YES:
                                    container = containerAcceptYes;
                                    break;
                                case MAYBE:
                                    container = containerAcceptMaybe;
                                    break;
                                case NO:
                                    container = containerAcceptNo;
                                    break;
                                default:
                                    container = containerAcceptNone;
                            }
                            container.setVisibility(View.VISIBLE);
                            container.addView(generateItem(participantWithUser));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ParticipantListActivity.this,
                                throwable);
                    }
                }));
    }

    private void init() {
        appbar.setTitle(getString(R.string.participant));

    }

    private ItemParticipantList generateItem(final ParticipantWithUser participantWithUser) {
        ItemParticipantList item = new ItemParticipantList(this);
        item.setParticipant(participantWithUser);
        return item;
    }
}
