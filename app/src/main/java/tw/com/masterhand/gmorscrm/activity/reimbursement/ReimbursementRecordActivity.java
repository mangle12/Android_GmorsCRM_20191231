package tw.com.masterhand.gmorscrm.activity.reimbursement;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.ReimbursementWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ExpRecordCard;

public class ReimbursementRecordActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_record);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateList();
    }

    private void init() {
        appbar.setTitle(getString(R.string.main_menu_exp_record));
    }

    private void updateList() {
        container.removeAllViews();
        mDisposable.add(DatabaseHelper.getInstance(this).getReimbursement().toSortedList(new Comparator<ReimbursementWithConfig>() {
            @Override
            public int compare(ReimbursementWithConfig o1, ReimbursementWithConfig o2) {
                return o2.getReimbursement().getCreated_at().compareTo(o1.getReimbursement().getCreated_at());
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ReimbursementWithConfig>>() {
            @Override
            public void accept(List<ReimbursementWithConfig> result) throws Exception {
                if (result.size() > 0) {
                    for (ReimbursementWithConfig reimbursement : result) {
                        ExpRecordCard card = new ExpRecordCard(ReimbursementRecordActivity.this);
                        card.setReocrd(reimbursement);
                        container.addView(card);
                    }
                } else {
                    container.addView(getEmptyImageView(null));
                }

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) throws Exception {
                ErrorHandler.getInstance().setException(ReimbursementRecordActivity.this, t);
                container.addView(getEmptyImageView(null));
            }
        }));
    }
}
