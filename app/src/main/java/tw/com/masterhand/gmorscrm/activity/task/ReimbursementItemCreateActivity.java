package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.ReimbursementTargetSelectActivity;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ReimbursementType;
import tw.com.masterhand.gmorscrm.enums.ReimbursementTypeLimit;
import tw.com.masterhand.gmorscrm.model.ReimbursementItemWithConfig;
import tw.com.masterhand.gmorscrm.model.ReimbursementTarget;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.setting.ConfigReimbursementItem;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectTime;

public class ReimbursementItemCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.itemSelectTime)
    ItemSelectTime itemSelectTime;
    @BindView(R.id.containerTarget)
    LinearLayout containerTarget;
    @BindView(R.id.tvTarget)
    TextView tvTarget;
    @BindView(R.id.tvQuota)
    TextView tvQuota;
    @BindView(R.id.tvLeft)
    TextView tvLeft;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.etNote)
    EditText etNote;
    @BindView(R.id.etAmount)
    EditTextWithTitle etAmount;
    @BindView(R.id.etTogether)
    EditTextWithTitle etTogether;

    ReimbursementItemWithConfig item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_item_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (item != null)
            return;

        String itemString = getIntent().getStringExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_ITEM);
        if (TextUtils.isEmpty(itemString)) {
            /*新建報銷項目*/
            String configString = getIntent().getStringExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_CONFIG);
            ConfigReimbursementItem config = gson.fromJson(configString, ConfigReimbursementItem.class);
            item = new ReimbursementItemWithConfig();
            item.setConfig(config);
            item.getItem().setConfig_reimbursement_item_id(config.getId());
            if (config.getType() == ReimbursementTypeLimit.SELF) {
                item.getItem().setType(ReimbursementType.SELF);
            } else if (config.getType() == ReimbursementTypeLimit.NOT_REQUIRED) {
                containerTarget.setVisibility(View.GONE);
            }
            if (item.getConfig().getLimit() == 0) {
                etAmount.setTitle(getString(R.string.exp_amount));
            } else {
                etAmount.setTitle(getString(R.string.exp_amount) + String.format(getString(R.string.exp_amount_limit), item.config.getLimit()));
            }
            tvName.setText(config.getName());
            tvTarget.setText(item.getItem().getType().getTitle());
        } else {
            /*編輯報銷項目*/
            getReimbursement(itemString);
        }
        updateQuota();
    }

    private void init() {
        itemSelectTime.setAllday(true);
        itemSelectTime.setStartTitle(getString(R.string.start_time));
        itemSelectTime.setEndTitle(getString(R.string.end_time));
        itemSelectTime.disableAlert();
        itemSelectTime.disableAllday();
        itemSelectCustomer.setShouldCheck(false);

        appbar.setTitle(getString(R.string.exp_item));//報銷項目
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點擊完成
                if (checkInput()) {
                    item.getItem().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    item.getItem().setFrom_date(itemSelectTime.getStart().getTime());
                    item.getItem().setTo_date(itemSelectTime.getEnd().getTime());
                    item.getItem().setAmount(Float.valueOf(etAmount.getText().toString()));
                    item.getItem().setTogether(etTogether.getText().toString());
                    item.getItem().setDescription(etNote.getText().toString());
                    item.getItem().setUpdated_at(new Date());
                    mDisposable.add(DatabaseHelper.getInstance(ReimbursementItemCreateActivity.this)
                            .saveReimbursementFile(itemSelectPhoto.getFiles()).toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<String>>() {
                                        @Override
                                        public void accept(List<String> strings) throws Exception {
                                            item.setFiles(strings);
                                            Intent intent = new Intent();
                                            intent.putExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_ITEM, gson.toJson(item));
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            ErrorHandler.getInstance().setException(ReimbursementItemCreateActivity.this, throwable);
                                        }
                                    }));
                }
            }
        });


    }

    private void getReimbursement(String itemString) {
        item = gson.fromJson(itemString, ReimbursementItemWithConfig.class);
        getCustomer(item.getItem().getCustomer_id());
        tvName.setText(item.getConfig().getName());
        itemSelectTime.setStart(item.getItem().getFrom_date());
        itemSelectTime.setEnd(item.getItem().getTo_date());
        itemSelectTime.updateTime();
        tvTarget.setText(item.getItem().getType().getTitle());
        if (item.getConfig().getLimit() == 0) {
            etAmount.setTitle(getString(R.string.exp_amount));
        } else {
            etAmount.setTitle(getString(R.string.exp_amount) + String.format(getString(R.string.exp_amount_limit), item.config.getLimit()));
        }
        etAmount.setText(String.valueOf(item.getItem().getAmount()));
        etNote.setText(item.getItem().getDescription());
        etTogether.setText(item.getItem().getTogether());
        mDisposable.add(DatabaseHelper.getInstance(this).getFileById(item.getFiles()).toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        itemSelectPhoto.setFiles(files);
                    }
                }));
    }

    private void getCustomer(String customerId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {

            @Override
            public void accept(Customer customer) throws Exception {
                itemSelectCustomer.setCustomer(customer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ReimbursementItemCreateActivity.this, throwable);
            }
        }));
    }

    private void updateQuota() {
        switch (item.getItem().getType()) {
            case SELF:
                mDisposable.add(DatabaseHelper.getInstance(this).getUserById(TokenManager.getInstance().getUser().getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        tvQuota.setVisibility(View.VISIBLE);
                        // 可報銷定額
                        tvQuota.setText(getString(R.string.exp_available) + NumberFormater.getMoneyFormat(user.getExpense_quota()) + getString(R
                                .string.money_unit) + "，");

                        // 剩餘金額
                        tvLeft.setText(getString(R.string.remain) + NumberFormater.getMoneyFormat(user.getExpense_left()) + getString(R.string.money_unit));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ReimbursementItemCreateActivity.this, throwable);
                    }
                }));

                break;
            case DEPARTMENT:
                mDisposable.add(DatabaseHelper.getInstance(this).getDepartmentById(TokenManager.getInstance().getUser().getDepartment_id())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Department>() {
                    @Override
                    public void accept(Department department) throws Exception {
                        tvQuota.setVisibility(View.VISIBLE);
                        if (TokenManager.getInstance().getUser().getId().equals(department.getManager_id()))
                        {
                            tvQuota.setText(getString(R.string.exp_available) + NumberFormater.getMoneyFormat(department.getExpense_quota()) + getString(R.string.money_unit) + "，");
                        }
                        else
                        {
                            tvQuota.setVisibility(View.GONE);
                        }
                        tvLeft.setText(getString(R.string.remain) + NumberFormater.getMoneyFormat(department.getExpense_left()) + getString(R.string.money_unit));
                    }
                }));
                break;
        }
    }

    /**
     * 檢查輸入資料
     */
    boolean checkInput() {
        String input = etAmount.getText().toString();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, getString(R.string.error_msg_required) + getString(R.string.exp_amount), Toast.LENGTH_LONG).show();
            return false;
        }
        if (item.getConfig().getLimit() > 0 && Double.valueOf(input) > item.getConfig().getLimit()) {
            Toast.makeText(this, getString(R.string.error_msg_reimbursement_limit), Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectCustomer.getVisibility() == View.VISIBLE && itemSelectCustomer.getCustomer() == null) {
            Logger.e(TAG, "customer is empty");
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (item.getConfig().getType() != ReimbursementTypeLimit.NOT_REQUIRED && item.getItem().getType() == ReimbursementType.NONE) {
            Toast.makeText(this, R.string.error_msg_no_target, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.tvTarget)
    void changeTarget() {
        Intent intent = new Intent(this, ReimbursementTargetSelectActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_CONFIG, item.getItem().getConfig_reimbursement_item_id());
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_TARGET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_SELECT_TARGET:
                    ReimbursementTarget target = gson.fromJson(data.getStringExtra(MyApplication.INTENT_KEY_TARGET), ReimbursementTarget.class);
                    item.getItem().setType(target.getType());

                    if (target.getType() == ReimbursementType.DEPARTMENT) {
                        item.getItem().setDepartment_id(target.getTargetId());
                    } else {
                        item.getItem().setDepartment_id("");
                    }

                    tvTarget.setText(target.getType().getTitle());
                    updateQuota();
                    break;
                default:
                    itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
                    itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
