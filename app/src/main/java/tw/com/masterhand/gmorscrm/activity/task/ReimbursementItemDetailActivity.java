package tw.com.masterhand.gmorscrm.activity.task;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.ReimbursementItemWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectTime;

public class ReimbursementItemDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
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
    @BindView(R.id.tvAmountTitle)
    TextView tvAmountTitle;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    @BindView(R.id.containerPhoto)
    LinearLayout containerPhoto;
    @BindView(R.id.tvTogether)
    TextView tvTogether;
    @BindView(R.id.tvNote)
    TextView tvNote;

    protected ReimbursementItemWithConfig item;
    protected String viewerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_item_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        String itemString = getIntent().getStringExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_ITEM);

        if (TextUtils.isEmpty(itemString)) {
            finish();
        } else {
            getReimbursement(itemString);
        }
    }

    private void init() {
        itemSelectTime.setAllday(true);
        itemSelectTime.disableAlert();
        itemSelectTime.disableAllday();
        viewerId = TokenManager.getInstance().getUser().getId();
        appbar.setTitle(getString(R.string.exp_item));
    }

    private void getReimbursement(String itemString) {
        item = gson.fromJson(itemString, ReimbursementItemWithConfig.class);
        getCustomer(item.getItem().getCustomer_id());
        tvName.setText(item.getConfig().getName());
        itemSelectTime.setStart(item.getItem().getFrom_date());
        itemSelectTime.setEnd(item.getItem().getTo_date());
        itemSelectTime.updateTime();
        itemSelectTime.disableEdit();
        tvTarget.setText(item.getItem().getType().getTitle());
        updateQuota();
        tvAmountTitle.setText(getString(R.string.exp_amount)+String.format(getString(R.string.exp_amount_limit), item.config.getLimit()));
        tvAmount.setText(NumberFormater.getMoneyFormat(item.getItem().getAmount()));

        if (!TextUtils.isEmpty(item.getItem().getTogether()))
            tvTogether.setText(item.getItem().getTogether());
        if (!TextUtils.isEmpty(item.getItem().getDescription()))
            tvNote.setText(item.getItem().getDescription());

        updatePhoto();
    }

    private void getCustomer(String customerId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {

            @Override
            public void accept(Customer customer) throws Exception {
                itemSelectCustomer.setCustomer(customer);
                itemSelectCustomer.disableSelectCustomer();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ReimbursementItemDetailActivity.this, throwable);
            }
        }));
    }

    private void updatePhoto() {
        containerPhoto.removeAllViews();
        if (item.getFiles().size() > 0) {
            for (String fileId : item.getFiles()) {
                mDisposable.add(DatabaseHelper.getInstance(this).getFileById(fileId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<File>() {

                    @Override
                    public void accept(File file) throws Exception {
                        containerPhoto.addView(generatePhotoItem(file));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ReimbursementItemDetailActivity.this, throwable);
                    }
                }));
            }
        } else {
            containerPhoto.addView(getEmptyTextView(getString(R.string.error_msg_no_photo)));
        }
    }

    private void updateQuota() {
        switch (item.getItem().getType()) {
            case NONE:
                containerTarget.setVisibility(View.GONE);
                break;
            case SELF:
                // 可報銷定額
                tvQuota.setText(getString(R.string.exp_available) + NumberFormater.getMoneyFormat(TokenManager.getInstance().getUser().getExpense_quota()) + getString(R
                        .string.money_unit) + "，" + getString(R.string.remain));

                // 剩餘金額
                tvLeft.setText(NumberFormater.getMoneyFormat(TokenManager.getInstance().getUser().getExpense_left()));
                break;
            case DEPARTMENT:
                mDisposable.add(DatabaseHelper.getInstance(this).getDepartmentById(TokenManager.getInstance().getUser().getDepartment_id())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Department>() {
                    @Override
                    public void accept(Department department) throws Exception {
                        tvQuota.setText(getString(R.string.exp_available) + NumberFormater.getMoneyFormat(department.getExpense_quota()));
                    }
                }));
                break;
        }
    }
}
