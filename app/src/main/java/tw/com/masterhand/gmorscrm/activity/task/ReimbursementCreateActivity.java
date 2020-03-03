package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.enums.ReimbursementApproval;
import tw.com.masterhand.gmorscrm.model.ReimbursementItemWithConfig;
import tw.com.masterhand.gmorscrm.model.ReimbursementWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Reimbursement;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigReimbursementItem;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.ItemSelectPeople;

public class ReimbursementCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.tvName)
    TextView tvName;// 報銷人
    @BindView(R.id.dropdownApproval)
    Dropdown dropdownApproval;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.tvCount)
    TextView tvCount;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.container)
    LinearLayout container;// 報銷項目容器

    ReimbursementWithConfig reimbursement;
    ReimbursementItemWithConfig selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        tvName.setText(TokenManager.getInstance().getUser().getShowName());

        if (reimbursement != null)
            return;

        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建報價*/
            reimbursement = new ReimbursementWithConfig();
            reimbursement.setReimbursement(new Reimbursement());
            reimbursement.getTrip().setFrom_date(new Date());
            updateExpList();
        } else {
            /*編輯報價*/
            getReimbursement(tripId);
        }
    }

    @Override
    public void onBackPressed() {
        if (reimbursement.getReimbursementItems().size() > 0) {
            if (TextUtils.isEmpty(reimbursement.getReimbursement().getId())) {
                for (ReimbursementItemWithConfig item : reimbursement.getReimbursementItems())
                {
                    Disposable disposable = DatabaseHelper.getInstance(this).deleteFileById(item.getFiles()).subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    // 背景線程執行刪除
                                    Logger.e("File Delete Process", "delete ReimbursementItems " + "file:" + aBoolean);
                                }
                            });
                }
            }
        }
        super.onBackPressed();
    }

    private void init() {
        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_reimbursement));//新建工作/報銷單
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點擊完成
                if (checkInput()) {
                    if (TextUtils.isEmpty(reimbursement.getTrip().getUser_id())) {
                        Logger.e(TAG, "user_id is null");
                        return;
                    }

                    reimbursement.getTrip().setDate_type(true);
                    if (dropdownApproval.getSelected() != Dropdown.VALUE_EMPTY) {
                        reimbursement.getReimbursement().setApproval(ReimbursementApproval.getTypeByCode(dropdownApproval.getSelected() + 1));
                    } else {
                        reimbursement.getReimbursement().setApproval(ReimbursementApproval.getTypeByCode(1));
                    }

                    Date start = null, end = null;
                    float amount = 0;
                    for (ReimbursementItemWithConfig item : reimbursement.getReimbursementItems()) {
                        amount += item.getItem().getAmount();
                        if (start == null) {
                            start = item.getItem().getFrom_date();
                        } else if (item.getItem().getFrom_date().compareTo(start) < 0) {
                            start = item.getItem().getFrom_date();
                        }
                        if (end == null) {
                            end = item.getItem().getTo_date();
                        } else if (item.getItem().getTo_date().compareTo(end) > 0) {
                            end = item.getItem().getTo_date();
                        }
                    }

                    reimbursement.getTrip().setName(TimeFormater.getInstance().toPeriodYearFormat(start, end) + " " + getString(R
                            .string
                            .exp_document));
                    reimbursement.getReimbursement().setAmount(amount);
                    mDisposable.add(DatabaseHelper.getInstance(ReimbursementCreateActivity.this).saveReimbursement(reimbursement)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    stopProgressDialog();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable t) throws Exception {
                                    stopProgressDialog();
                                    Toast.makeText(ReimbursementCreateActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });

        //審批級數
        String[] approvals = new String[ReimbursementApproval.values().length];
        for (int i = 0; i < approvals.length; i++) {
            approvals[i] = getString(ReimbursementApproval.values()[i].getTitle());
        }
        dropdownApproval.setItems(approvals);
    }

    /**
         * 取得報銷單
         */
    private void getReimbursement(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getReimbursementByTrip(tripId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ReimbursementWithConfig>() {
            @Override
            public void accept(ReimbursementWithConfig result) throws Exception {
                reimbursement = result;
                if (reimbursement.getReimbursement().getApproval() != null)
                    dropdownApproval.select(reimbursement.getReimbursement().getApproval().getCode() - 1);

                updateExpList();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ReimbursementCreateActivity.this, throwable);
                finish();
            }
        }));
    }

    /**
     * 檢查輸入資料
     */
    boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        for (ReimbursementItemWithConfig item : reimbursement.getReimbursementItems()) {
            if (item.getItem().getAmount() == 0) {
                Toast.makeText(this, R.string.error_msg_no_reimbursement_amount, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (dropdownApproval.getVisibility() == View.VISIBLE && dropdownApproval.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.special_price_approval), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /**
     * 更新報銷總額
     */
    void updateTotal() {
        float total = 0;
        if (reimbursement.getReimbursementItems().size() > 0)
            for (ReimbursementItemWithConfig item : reimbursement.getReimbursementItems()) {
                total += item.getItem().getAmount();
            }
        reimbursement.getReimbursement().setAmount(total);
        tvTotal.setText(NumberFormater.getMoneyFormat(total));
    }

    /**
     * 更新報銷項目
     */
    void updateExpList() {
        container.removeAllViews();
        tvCount.setText(getString(R.string.exp_count1) + reimbursement.getReimbursementItems().size() + getString(R.string.exp_count2));
        if (reimbursement.getReimbursementItems().size() > 0) {
            for (final ReimbursementItemWithConfig item : reimbursement.getReimbursementItems()) {
                if (item.getItem().getAmount() > 0 && item.getItem().getDeleted_at() == null)
                    container.addView(generateItem(item));
            }
        }
        updateTotal();
    }

    private View generateItem(final ReimbursementItemWithConfig item) {
        View view = getLayoutInflater().inflate(R.layout.item_exp_list, container, false);
        TextView tvDate = view.findViewById(  R.id.tvDate);
        TextView tvTitle = view.findViewById( R.id.tvTitle);
        TextView tvAmount = view.findViewById(  R.id.tvAmount);
        ImageButton btnDelete = view.findViewById( R.id.btnFunc);

        tvTitle.setText(item.getConfig().getName());
        tvDate.setText(TimeFormater.getInstance().toPeriodYearFormat(item.getItem().getFrom_date(), item.getItem().getTo_date()));
        tvAmount.setText(NumberFormater.getMoneyFormat(item.getItem().getAmount()));

        // 點擊刪除後更新列表及總額
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.getItem().setDeleted_at(new Date());
                updateExpList();
                updateTotal();
            }
        });

        // 點擊編輯
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = item;
                Intent intent = new Intent(ReimbursementCreateActivity.this, ReimbursementItemCreateActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_ITEM, gson.toJson(selected));
                startActivityForResult(intent, MyApplication.REQUEST_EDIT_EXP);
            }
        });
        return view;
    }

    /**
         * 增加報銷項目
         */
    @OnClick(R.id.btnAdd)
    void addItem() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigReimbursementItem()
                .observeOn(AndroidSchedulers.mainThread()).toList()
                .subscribe(new Consumer<List<ConfigReimbursementItem>>() {
            @Override
            public void accept(final List<ConfigReimbursementItem> configReimbursementItems) throws Exception {
                String[] items = new String[configReimbursementItems.size()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = configReimbursementItems.get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ReimbursementCreateActivity.this, MyApplication.DIALOG_STYLE);
                builder.setTitle(R.string.exp_item);//報銷項目
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfigReimbursementItem configReimbursementItem = configReimbursementItems.get(which);
                        Intent intent = new Intent(ReimbursementCreateActivity.this, ReimbursementItemCreateActivity.class);
                        intent.putExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_CONFIG, gson.toJson(configReimbursementItem));
                        startActivityForResult(intent, MyApplication.REQUEST_ADD_EXP);
                    }
                });

                builder.create().show();
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_ADD_EXP:
                    String addString = data.getStringExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_ITEM);
                    ReimbursementItemWithConfig addItem = gson.fromJson(addString, ReimbursementItemWithConfig.class);
                    reimbursement.getReimbursementItems().add(addItem);
                    updateExpList();
                    break;
                case MyApplication.REQUEST_EDIT_EXP:
                    String editString = data.getStringExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_ITEM);
                    ReimbursementItemWithConfig editItem = gson.fromJson(editString, ReimbursementItemWithConfig.class);
                    reimbursement.getReimbursementItems().remove(selected);
                    selected = null;
                    reimbursement.getReimbursementItems().add(editItem);
                    updateExpList();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
