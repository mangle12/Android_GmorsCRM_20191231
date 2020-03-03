package tw.com.masterhand.gmorscrm.activity.sale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.model.SalesOpportunityWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunity;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunitySub;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityLoseType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityWinType;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunitySub;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.ExpandableSale;
import tw.com.masterhand.gmorscrm.view.ItemSaleEdit;

public class SaleEditActivity extends BaseUserCheckActivity implements ExpandableSale
        .OnCheckChangeListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.dropdownLose)
    Dropdown dropdownLose;
    @BindView(R.id.dropdownWin)
    Dropdown dropdownWin;
    @BindView(R.id.etDecision)
    EditText etDecision;
    @BindView(R.id.etNote)
    EditText etNote;
    @BindView(R.id.etReason)
    EditText etReason;
    @BindView(R.id.containerReason)
    LinearLayout containerReason;

    String projectId;
    SalesOpportunityWithConfig result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_edit);
        projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        String id = getIntent().getStringExtra(MyApplication.INTENT_KEY_ID);
        if (TextUtils.isEmpty(id)) {
            /*新建銷售機會*/
            appbar.setCompleteListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveOpportunity();
                }
            });
            result = new SalesOpportunityWithConfig();
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigSalesOpportunityLoseType()
                    .toList().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigSalesOpportunityLoseType>>() {

                        @Override
                        public void accept(List<ConfigSalesOpportunityLoseType>
                                                   result)
                                throws Exception {
                            String[] items = new String[result.size()];
                            for (int i = 0; i < items.length; i++) {
                                items[i] = result.get(i).getName();
                            }
                            dropdownLose.setItems(items);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(SaleEditActivity.this,
                                    throwable);
                        }
                    }));
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigSalesOpportunityWinType()
                    .toList().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigSalesOpportunityWinType>>() {

                        @Override
                        public void accept(List<ConfigSalesOpportunityWinType>
                                                   result)
                                throws Exception {
                            String[] items = new String[result.size()];
                            for (int i = 0; i < items.length; i++) {
                                items[i] = result.get(i).getName();
                            }
                            dropdownWin.setItems(items);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(SaleEditActivity.this,
                                    throwable);
                        }
                    }));
            getOpportunity();
        } else {
            /*顯示銷售機會*/
            appbar.disable();
            etDecision.setEnabled(false);
            etReason.setEnabled(false);
            etNote.setEnabled(false);
            dropdownLose.disable();
            dropdownWin.disable();
            mDisposable.add(DatabaseHelper.getInstance(this).getSalesOpportunityWithConfig(id)
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<SalesOpportunityWithConfig>() {

                        @Override
                        public void accept(SalesOpportunityWithConfig salesOpportunityWithConfig)
                                throws
                                Exception {
                            result = salesOpportunityWithConfig;
                            Logger.e(TAG, "result:" + gson.toJson(result.getSalesOpportunity()));
                            SalesOpportunity salesOpportunity = result.getSalesOpportunity();
                            if (salesOpportunity.getDepartment_sales_opportunity() == null) {
                                Logger.e(TAG, "can't get Department_sales_opportunity:" +
                                        salesOpportunity.getId());
                                showNotFindSettingDialog();
                                return;
                            }
                            for (SalesOpportunitySub sub : result.getSalesOpportunitySubs()) {
                                if (sub.getDepartment_sales_opportunity() == null) {
                                    Logger.e(TAG, "can't get sub Department_sales_opportunity:" +
                                            sub.getId());
                                    showNotFindSettingDialog();
                                    return;
                                }
                            }
                            if (salesOpportunity.getPercentage() < 0) {
                                dropdownLose.setVisibility(View.VISIBLE);
                                dropdownLose.setText(salesOpportunityWithConfig
                                        .getSalesOpportunity()
                                        .getSales_opportunity_lose_type());
                                containerReason.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(salesOpportunityWithConfig
                                        .getSalesOpportunity
                                                ().getReason()))
                                    etReason.setText(salesOpportunityWithConfig
                                            .getSalesOpportunity().getReason());
                            } else if (salesOpportunity.getPercentage() == 100) {
                                dropdownWin.setVisibility(View.VISIBLE);
                                dropdownWin.setText(salesOpportunityWithConfig
                                        .getSalesOpportunity().getSales_opportunity_win_type());
                                containerReason.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(salesOpportunityWithConfig
                                        .getSalesOpportunity
                                                ().getReason()))
                                    etReason.setText(salesOpportunityWithConfig
                                            .getSalesOpportunity().getReason());
                            }
                            if (!TextUtils.isEmpty(salesOpportunity.getDecision_elements()))
                                etDecision.setText(salesOpportunity.getDecision_elements());
                            if (!TextUtils.isEmpty(salesOpportunity.getDescription()))
                                etNote.setText(salesOpportunity.getDescription());
                            updateList(false);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(SaleEditActivity.this,
                                    throwable);
                        }
                    }));
        }
    }

    private void init() {
        startProgressDialog();
        appbar.setTitle(getString(R.string.title_activity_sale_edit));
    }

    void showNotFindSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false).setMessage(R.string.error_msg_sales_opportunity_not_found)
                .setPositiveButton(R.string.confirm, new
                        DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
        builder.create().show();
    }

    private void updateList(boolean editable) {
        Logger.e(TAG, "updateList:" + editable);
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status == ProjectStatus.START)
                continue;
            ExpandableSale expandableSale = new ExpandableSale(this);
            expandableSale.setDepartmentSalesOpportunity(status, result.getSalesOpportunity()
                    .getDepartment_sales_opportunity());
            if (editable)
                expandableSale.setOnCheckChangeListener(this);
            else
                expandableSale.disableRadioClick();
            int total = 0;
            for (SalesOpportunitySub sub : result.getSalesOpportunitySubs()) {
                if (sub.getDepartment_sales_opportunity().getStage() == status.getValue()) {
                    total += sub.getDepartment_sales_opportunity().getPercentage();
                }
            }
            for (SalesOpportunitySub sub : result.getSalesOpportunitySubs()) {
                if (sub.getDepartment_sales_opportunity().getStage() == status.getValue()) {
                    ItemSaleEdit item = new ItemSaleEdit(this);
                    item.setSalesOpportunitySub(sub);
                    item.setEditable(editable);
                    item.setTotalPercent(total);
                    expandableSale.addListView(item);
                }
            }
            container.addView(expandableSale);
            if (result.getSalesOpportunity().getStage() == ProjectStatus.LOSE) {
                if (result.getSalesOpportunity().getStage() == status) {
                    expandableSale.setRadioCheck(true);
                } else {
                    expandableSale.setCount(getString(R.string.uncompleted));
                }
            } else {
                if (result.getSalesOpportunity().getStage().getValue() >= status.getValue()) {
                    expandableSale.setRadioCheck(true);
                } else {
                    expandableSale.setCount(getString(R.string.uncompleted));
                }
            }
        }
        stopProgressDialog();
    }

    private void saveOpportunity() {
        if (!checkInput())
            return;
        startProgressDialog();
        /*銷售階段*/
        result.getSalesOpportunity().setProject_id(projectId);
        result.getSalesOpportunity().setUser_id(TokenManager.getInstance().getUser().getId());

        int percent = 0;
        if (((ExpandableSale) container.getChildAt(4)).isRadioCheck()) {
            percent = 100;
        } else if (((ExpandableSale) container.getChildAt(5)).isRadioCheck()) {
            percent = -1;
        } else {
            for (int i = 0; i < 4; i++) {
                ExpandableSale item = (ExpandableSale) container.getChildAt(i);
                if (item.isRadioCheck()) {
                    percent = item.getPercent();
                } else {
                    int itemPercent = item.getResultPercent();
                    Logger.e(TAG, "itemPercent:" + itemPercent);
                    percent += itemPercent;
                }
                Logger.e(TAG, "percent:" + percent);
            }
        }
        List<SalesOpportunitySub> subs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ExpandableSale item = (ExpandableSale) container.getChildAt(i);
            for (SalesOpportunitySub salesOpportunitySub : item.getSalesOpportunitySub()) {
                subs.add(salesOpportunitySub);
            }
        }
        result.setSalesOpportunitySubs(subs);
        result.getSalesOpportunity().setStage(ProjectStatus.getProjectStatusByPercent(percent,
                result.getSalesOpportunity().getDepartment_sales_opportunity()));
        result.getSalesOpportunity().setPercentage(percent);
        String stageName = result.getSalesOpportunity().getDepartment_sales_opportunity()
                .getNameByStage(this, result.getSalesOpportunity().getStage());
        result.getSalesOpportunity().setName(getString(R.string.change_to) + "「" + stageName + "」");
        /*其他要素*/
        result.getSalesOpportunity().setDecision_elements(etDecision.getText().toString());
        result.getSalesOpportunity().setReason(etReason.getText().toString());
        if (dropdownLose.getSelected() != Dropdown.VALUE_EMPTY)
            result.getSalesOpportunity().setSales_opportunity_lose_type(dropdownLose.getText());
        if (dropdownWin.getSelected() != Dropdown.VALUE_EMPTY)
            result.getSalesOpportunity().setSales_opportunity_win_type(dropdownWin.getText());
        result.getSalesOpportunity().setDescription(etNote.getText().toString());
        Logger.e(TAG, "result:" + gson.toJson(result));

        mDisposable.add(DatabaseHelper.getInstance(this).saveSalesOpportunity(result).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {

            @Override
            public void accept(String s) throws Exception {
                stopProgressDialog();
                setResult(Activity.RESULT_OK);
                finish();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                stopProgressDialog();
                ErrorHandler.getInstance().setException(SaleEditActivity.this, throwable);
            }
        }));
    }

    private boolean checkInput() {
        if (((ExpandableSale) container.getChildAt(5)).isRadioCheck()) {
            if (TextUtils.isEmpty(dropdownLose.getText())) {
                Toast.makeText(this, getString(R.string.error_msg_required) + getString(R.string
                        .lose_reason), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (((ExpandableSale) container.getChildAt(4)).isRadioCheck()) {
            if (TextUtils.isEmpty(dropdownLose.getText())) {
                Toast.makeText(this, getString(R.string.error_msg_required) + getString(R.string
                        .win_reason), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onCheckChange(ExpandableSale item, boolean isChecked) {
        dropdownLose.setVisibility(View.GONE);
        dropdownWin.setVisibility(View.GONE);
        containerReason.setVisibility(View.GONE);
        if (item.getPercent() == 0) {
            setAllSelected(5, false);
            dropdownLose.setVisibility(View.VISIBLE);
            containerReason.setVisibility(View.VISIBLE);
            return;
        } else if (item.getPercent() == 100) {
            setAllSelected(4, true);
            ((ExpandableSale) container.getChildAt(5)).setRadioCheck(false);
            dropdownWin.setVisibility(View.VISIBLE);
            containerReason.setVisibility(View.VISIBLE);
            return;
        }
        ((ExpandableSale) container.getChildAt(5)).setRadioCheck(false);
        if (isChecked) {
            for (int i = 0; i < 5; i++) {
                ExpandableSale expandableSale = (ExpandableSale) container.getChildAt(i);
                if (expandableSale.getPercent() <= item.getPercent())
                    expandableSale.setRadioCheck(true);
                else
                    expandableSale.setRadioCheck(false);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                ExpandableSale expandableSale = (ExpandableSale) container.getChildAt(i);
                if (expandableSale.getPercent() >= item.getPercent())
                    expandableSale.setRadioCheck(false);
                else
                    expandableSale.setRadioCheck(true);
            }
        }
    }

    /**
     * 設定全選
     *
     * @param count        從頭到第幾項要全選
     * @param shouldSelect 全選或取消
     */
    private void setAllSelected(int count, boolean shouldSelect) {
        for (int i = 0; i < count; i++) {
            ExpandableSale expandableSale = (ExpandableSale) container.getChildAt(i);
            expandableSale.setRadioCheck(shouldSelect);
        }
    }

    /**
     * 取得銷售機會設定
     */
    private void getOpportunity() {
        Logger.e(TAG, "getOpportunity");
        mDisposable.add(DatabaseHelper.getInstance(this).getDepartmentSalesOpportunityByParent
                (TokenManager.getInstance().getUser()
                        .getCompany_id
                        ()).subscribe
                (new Consumer<DepartmentSalesOpportunity>
                        () {

                    @Override
                    public void accept(DepartmentSalesOpportunity departmentSalesOpportunity) throws
                            Exception {
                        result.getSalesOpportunity().setDepartment_sales_opportunity
                                (departmentSalesOpportunity);
                        getOpportunitySub();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(SaleEditActivity.this, throwable);
                    }
                }));
    }

    /**
     * 取得銷售機會小項設定
     */
    private void getOpportunitySub() {
        Logger.e(TAG, "getOpportunitySub");
        mDisposable.add(DatabaseHelper.getInstance(this).getDepartmentSalesOpportunitySub(result
                .getSalesOpportunity().getDepartment_sales_opportunity().getId())
                .observeOn(AndroidSchedulers.mainThread()).toList().observeOn(AndroidSchedulers
                        .mainThread()).subscribe(new Consumer<List<DepartmentSalesOpportunitySub>>() {
                    @Override
                    public void accept(List<DepartmentSalesOpportunitySub>
                                               departmentSalesOpportunitySubs) throws Exception {
                        for (DepartmentSalesOpportunitySub departmentSalesOpportunitySub :
                                departmentSalesOpportunitySubs) {
                            SalesOpportunitySub sub = new SalesOpportunitySub();
                            sub.setDepartment_sales_opportunity(departmentSalesOpportunitySub);
                            result.getSalesOpportunitySubs().add(sub);
                        }
                        checkPrevious();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(SaleEditActivity.this, throwable);
                    }
                }));
    }

    private void checkPrevious() {
        Logger.e(TAG, "checkPrevious");
        mDisposable.add(DatabaseHelper.getInstance(this).getLastSalesOpportunityByProject
                (projectId).map(new Function<SalesOpportunityWithConfig, Boolean>() {
            @Override
            public Boolean apply(SalesOpportunityWithConfig salesOpportunityWithConfig) throws
                    Exception {
                boolean isError = false;
                if (salesOpportunityWithConfig.getSalesOpportunity() != null) {
                    result.getSalesOpportunity().setStage(salesOpportunityWithConfig
                            .getSalesOpportunity().getStage());
                    result.getSalesOpportunity().setPercentage(salesOpportunityWithConfig
                            .getSalesOpportunity().getPercentage());
                    result.getSalesOpportunity().setSales_opportunity_lose_type
                            (salesOpportunityWithConfig.getSalesOpportunity()
                                    .getSales_opportunity_lose_type());
//                    result.getSalesOpportunity().setDecision_elements(salesOpportunityWithConfig
//                            .getSalesOpportunity().getDecision_elements());
//                    result.getSalesOpportunity().setReason(salesOpportunityWithConfig
//                            .getSalesOpportunity().getReason());
//                    result.getSalesOpportunity().setDescription(salesOpportunityWithConfig
//                            .getSalesOpportunity().getDescription());
                    Logger.e(TAG, "SalesOpportunity:" + gson.toJson(result.getSalesOpportunity()));
                }
                for (SalesOpportunitySub sub : result.getSalesOpportunitySubs()) {
                    boolean isNewSub = true;
                    for (SalesOpportunitySub oldSub : salesOpportunityWithConfig
                            .getSalesOpportunitySubs()) {
                        if (oldSub.getDepartment_sales_opportunity() == null) {
                            Logger.e(TAG, "無法取得小項設定");
                            isError = true;
                            isNewSub = false;
                            break;
                        }
                        if (sub.getDepartment_sales_opportunity().getId().equals(oldSub
                                .getDepartment_sales_opportunity().getId())) {
                            isNewSub = false;
                            if (sub.getDepartment_sales_opportunity().getUpdated_at().compareTo
                                    (oldSub.getDepartment_sales_opportunity().getUpdated_at()) >
                                    0) {
                                Logger.e(TAG, "小項有更新");
                                isError = true;
                                break;
                            }
                            break;
                        }
                    }
                    if (isNewSub) {
                        Logger.e(TAG, "小項有新增");
                        isError = true;
                        break;
                    }
                }
                if (!isError)
                    result.setSalesOpportunitySubs(salesOpportunityWithConfig
                            .getSalesOpportunitySubs());
                return isError;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {

            @Override
            public void accept(Boolean isError) throws
                    Exception {
                SalesOpportunity salesOpportunity = result.getSalesOpportunity();
                if (!TextUtils.isEmpty(salesOpportunity.getDecision_elements()))
                    etDecision.setText(salesOpportunity.getDecision_elements());
                if (!TextUtils.isEmpty(salesOpportunity.getDescription()))
                    etNote.setText(salesOpportunity.getDescription());
                if (salesOpportunity.getPercentage() < 0) {
                    dropdownLose.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(salesOpportunity.getSales_opportunity_lose_type()))
                        dropdownLose.setText(salesOpportunity.getSales_opportunity_lose_type());
                    containerReason.setVisibility(View.VISIBLE);
                    etReason.setText(salesOpportunity.getReason());
                } else if (salesOpportunity.getPercentage() == 100) {
                    dropdownWin.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(salesOpportunity.getSales_opportunity_win_type()))
                        dropdownWin.setText(salesOpportunity.getSales_opportunity_win_type());
                    containerReason.setVisibility(View.VISIBLE);
                    etReason.setText(salesOpportunity.getReason());
                }
                updateList(true);

                if (isError) {
                    Toast.makeText(SaleEditActivity.this, R.string
                            .error_msg_sales_opportunity_update, Toast.LENGTH_LONG).show();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                /*無前筆紀錄*/
                ErrorHandler.getInstance().setException(SaleEditActivity.this, throwable);
                updateList(true);
            }
        }));
    }
}
