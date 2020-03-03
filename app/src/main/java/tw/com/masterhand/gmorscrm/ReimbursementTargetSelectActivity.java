package tw.com.masterhand.gmorscrm;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.ConfigReimbursementItem;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemReimbursementTarget;

public class ReimbursementTargetSelectActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_target_select);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setTitle(getString(R.string.select_exp_target));
        String config = getIntent().getStringExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_CONFIG);
        if (TextUtils.isEmpty(config)) {
            Logger.e(TAG, "config is empty.");
            finish();
            return;
        }
        startProgressDialog();
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigReimbursementItemById(config)
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<ConfigReimbursementItem>() {

                    @Override
                    public void accept(ConfigReimbursementItem configReimbursementItem) throws
                            Exception {
                        switch (configReimbursementItem.getType()) {
                            case NONE:
                                addSelf();
                                addDepartment();
                                break;
                            case SELF:
                                addSelf();
                                break;
                            case DEPARTMENT:
                                addDepartment();
                                break;
                        }
                        stopProgressDialog();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        stopProgressDialog();
                        Logger.e(TAG, "error:" + throwable.getMessage());
                        finish();
                    }
                }));
    }

    private void addSelf() {
        mDisposable.add(DatabaseHelper.getInstance(this).getUserById(TokenManager.getInstance()
                .getUser().getId()
        ).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                ItemReimbursementTarget item = new ItemReimbursementTarget
                        (ReimbursementTargetSelectActivity.this);
                item.setTarget(user);
                container.addView(item);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ReimbursementTargetSelectActivity.this,
                        throwable);
            }
        }));

    }

    private void addDepartment() {
        mDisposable.add(DatabaseHelper.getInstance(this).getDepartmentById(TokenManager
                .getInstance().getUser()
                .getDepartment_id()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Department>() {
            @Override
            public void accept(Department department) throws Exception {
                ItemReimbursementTarget item = new ItemReimbursementTarget
                        (ReimbursementTargetSelectActivity.this);
                item.setTarget(department);
                container.addView(item);
                stopProgressDialog();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ReimbursementTargetSelectActivity.this,
                        throwable);
            }
        }));
    }

}
