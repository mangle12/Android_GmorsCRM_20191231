package tw.com.masterhand.gmorscrm.activity.report;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ReportTotalType;
import tw.com.masterhand.gmorscrm.model.ReportSummary;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.MonthReport;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.tools.TranslateHelper;
import tw.com.masterhand.gmorscrm.view.ItemReportTotal;

public class MonthReportActivity extends BaseUserCheckActivity {
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.container_total)
    LinearLayout containerTotal;
    @BindView(R.id.tvPeriod)
    TextView tvPeriod;
    @BindView(R.id.etNextPlan)
    EditText etNextPlan;
    @BindView(R.id.btnTranslation)
    ImageButton btnTranslation;
    /*振豪*/
    @BindView(R.id.containerCurrentCustomer)
    LinearLayout containerCurrentCustomer;
    @BindView(R.id.containerCurrentProject)
    LinearLayout containerCurrentProject;
    @BindView(R.id.containerCurrentQuestion)
    LinearLayout containerCurrentQuestion;
    @BindView(R.id.containerNextVisit)
    LinearLayout containerNextVisit;
    @BindView(R.id.containerNextGoal)
    LinearLayout containerNextGoal;
    @BindView(R.id.containerNextGuide)
    LinearLayout containerNextGuide;
    @BindView(R.id.etCurrentCustomer)
    EditText etCurrentCustomer;
    @BindView(R.id.etCurrentProject)
    EditText etCurrentProject;
    @BindView(R.id.etCurrentQuestion)
    EditText etCurrentQuestion;
    @BindView(R.id.etNextVisit)
    EditText etNextVisit;
    @BindView(R.id.etNextGoal)
    EditText etNextGoal;
    @BindView(R.id.etNextGuide)
    EditText etNextGuide;
    /*胤舜*/
    @BindView(R.id.containerSaleGoal)
    LinearLayout containerSaleGoal;
    @BindView(R.id.containerCurrentGoal)
    LinearLayout containerCurrentGoal;
    @BindView(R.id.containerImportantSituation)
    LinearLayout containerImportantSituation;
    @BindView(R.id.containerPotentialMarket)
    LinearLayout containerPotentialMarket;
    @BindView(R.id.containerCompeteStrategy)
    LinearLayout containerCompeteStrategy;
    @BindView(R.id.containerResourceInvestment)
    LinearLayout containerResourceInvestment;
    @BindView(R.id.etSaleGoal)
    EditText etSaleGoal;
    @BindView(R.id.etCurrentGoal)
    EditText etCurrentGoal;
    @BindView(R.id.etImportantSituation)
    EditText etImportantSituation;
    @BindView(R.id.etPotentialMarket)
    EditText etPotentialMarket;
    @BindView(R.id.etCompeteStrategy)
    EditText etCompeteStrategy;
    @BindView(R.id.etResourceInvestment)
    EditText etResourceInvestment;

    Date start, end;
    String time;
    String viewerId;
    MonthReport monthReport;

    private MonthReport translation = new MonthReport();
    private final String split = "_@_";
    private boolean isTranslationMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_report);
        init();
    }

    @Override
    protected void onUserChecked() {
        if (TokenManager.getInstance().getUser().getIs_translate())
        {
            btnTranslation.setVisibility(View.VISIBLE);//翻譯
        }

        getHiddenField();
        updateStatistic();

        if (!TokenManager.getInstance().getUser().getId().equals(viewerId)) {
            btnSubmit.setVisibility(View.GONE);//提交
        }

        try {
            start = TimeFormater.getInstance().fromDatabaseFormat(getIntent().getStringExtra(MyApplication.INTENT_KEY_DATE_START));
            end = TimeFormater.getInstance().fromDatabaseFormat(getIntent().getStringExtra(MyApplication.INTENT_KEY_DATE_END));
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTime(start);
            cal.add(Calendar.DATE, 15);
            time = TimeFormater.getInstance().toMonthReportFormat(cal.getTime());
            //本月工作彙總
            tvPeriod.setText(getString(R.string.month_work_summary) + " " + TimeFormater.getInstance().toMonthDayFormat(start) + "~" + TimeFormater.getInstance().toMonthDayFormat(end));
            //取得月報
            mDisposable.add(DatabaseHelper.getInstance(this).getMonthReport(time, viewerId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MonthReport>() {

                        @Override
                        public void accept(MonthReport report) throws Exception {
                            monthReport = report;
                            if (monthReport.getSubmit()) {
                                btnSubmit.setVisibility(View.GONE);
                                etNextPlan.setEnabled(false);
                                etCurrentCustomer.setEnabled(false);
                                etCurrentProject.setEnabled(false);
                                etCurrentQuestion.setEnabled(false);
                                etNextVisit.setEnabled(false);
                                etNextGoal.setEnabled(false);
                                etNextGuide.setEnabled(false);
                                etSaleGoal.setEnabled(false);
                                etCurrentGoal.setEnabled(false);
                                etImportantSituation.setEnabled(false);
                                etPotentialMarket.setEnabled(false);
                                etCompeteStrategy.setEnabled(false);
                                etResourceInvestment.setEnabled(false);
                            }
                            if (!TextUtils.isEmpty(monthReport.getCurrent_month_customer()))
                                etCurrentCustomer.setText(monthReport.getCurrent_month_customer());
                            if (!TextUtils.isEmpty(monthReport.getCurrent_month_project()))
                                etCurrentProject.setText(monthReport.getCurrent_month_project());
                            if (!TextUtils.isEmpty(monthReport.getCurrent_month_question()))
                                etCurrentQuestion.setText(monthReport.getCurrent_month_question());
                            if (!TextUtils.isEmpty(monthReport.getNext_month_plan()))
                                etNextPlan.setText(monthReport.getNext_month_plan());
                            if (!TextUtils.isEmpty(monthReport.getNext_month_visit_plan()))
                                etNextVisit.setText(monthReport.getNext_month_visit_plan());
                            if (!TextUtils.isEmpty(monthReport.getNext_month_sales_goal()))
                                etNextGoal.setText(monthReport.getNext_month_sales_goal());
                            if (!TextUtils.isEmpty(monthReport.getNext_month_guide()))
                                etNextGuide.setText(monthReport.getNext_month_guide());
                            if (!TextUtils.isEmpty(monthReport.getSale_goal()))
                                etSaleGoal.setText(monthReport.getSale_goal());
                            if (!TextUtils.isEmpty(monthReport.getCurrent_goal()))
                                etCurrentGoal.setText(monthReport.getCurrent_goal());
                            if (!TextUtils.isEmpty(monthReport.getImportant_situation()))
                                etImportantSituation.setText(monthReport.getImportant_situation());
                            if (!TextUtils.isEmpty(monthReport.getPotential_market()))
                                etPotentialMarket.setText(monthReport.getPotential_market());
                            if (!TextUtils.isEmpty(monthReport.getCompete_strategy()))
                                etCompeteStrategy.setText(monthReport.getCompete_strategy());
                            if (!TextUtils.isEmpty(monthReport.getResource_investment()))
                                etResourceInvestment.setText(monthReport.getResource_investment());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            // 無月報或錯誤
                            ErrorHandler.getInstance().setException(MonthReportActivity.this, throwable);
                        }
                    }));
        } catch (ParseException e) {
            Logger.e(TAG, e.getMessage());
            finish();
        }
    }

    private void init() {
        monthReport = new MonthReport();
        viewerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_USER);
        if (TextUtils.isEmpty(viewerId)) {
            finish();
        }
        Logger.e(TAG, "viewerId:" + viewerId);
    }

    //返回
    @OnClick(R.id.btnBack)
    @Override
    public void onBackPressed() {
        if (TokenManager.getInstance().getUser().getId().equals(viewerId) && !monthReport.getSubmit()) {
            saveMonthReport();
        } else {
            super.onBackPressed();
        }
    }

    //提交
    @OnClick(R.id.btnSubmit)
    void submit() {
        if (checkInput() && !monthReport.getSubmit()) {
            showConfirmSubmitDialog();
        }
    }

    //翻譯
    @OnClick(R.id.btnTranslation)
    void translateReport() {
        if (isTranslationMode) {
            setTranslationMode(false);
            updateTranslation(monthReport);
        } else {
            if (TextUtils.isEmpty(translation.id)) {
                ArrayList<String> inputList = new ArrayList<>();
                if (TextUtils.isEmpty(monthReport.getCurrent_month_customer()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getCurrent_month_customer());

                if (TextUtils.isEmpty(monthReport.getCurrent_month_project()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getCurrent_month_project());

                if (TextUtils.isEmpty(monthReport.getCurrent_month_question()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getCurrent_month_question());

                if (TextUtils.isEmpty(monthReport.getNext_month_plan()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getNext_month_plan());

                if (TextUtils.isEmpty(monthReport.getNext_month_visit_plan()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getNext_month_visit_plan());

                if (TextUtils.isEmpty(monthReport.getNext_month_sales_goal()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getNext_month_sales_goal());

                if (TextUtils.isEmpty(monthReport.getNext_month_guide()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getNext_month_guide());

                if (TextUtils.isEmpty(monthReport.getSale_goal()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getSale_goal());

                if (TextUtils.isEmpty(monthReport.getCurrent_goal()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getCurrent_goal());

                if (TextUtils.isEmpty(monthReport.getImportant_situation()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getImportant_situation());

                if (TextUtils.isEmpty(monthReport.getPotential_market()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getPotential_market());

                if (TextUtils.isEmpty(monthReport.getCompete_strategy()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getCompete_strategy());

                if (TextUtils.isEmpty(monthReport.getResource_investment()))
                    inputList.add("empty");
                else
                    inputList.add(monthReport.getResource_investment());

                String input = StringUtils.join(inputList, split);
                Logger.e(TAG, "input:" + input);
                TranslateHelper translateHelper = new TranslateHelper();
                translateHelper.startTranslate(input, new TranslateHelper.TranslateListener() {
                    @Override
                    public void onTranslateFinish(String result) {
                        Logger.e(TAG, "result:" + result);
                        String[] resultList = result.replace(" ", "").split(split);
                        translation.setCurrent_month_customer(resultList[0]);
                        translation.setCurrent_month_project(resultList[1]);
                        translation.setCurrent_month_question(resultList[2]);
                        translation.setNext_month_plan(resultList[3]);
                        translation.setNext_month_visit_plan(resultList[4]);
                        translation.setNext_month_sales_goal(resultList[5]);
                        translation.setNext_month_guide(resultList[6]);
                        translation.setSale_goal(resultList[7]);
                        translation.setCurrent_goal(resultList[8]);
                        translation.setImportant_situation(resultList[9]);
                        translation.setPotential_market(resultList[10]);
                        translation.setCompete_strategy(resultList[11]);
                        translation.setResource_investment(resultList[12]);

                        translation.id = monthReport.id;
                        setTranslationMode(true);
                        updateTranslation(translation);
                    }

                    @Override
                    public void onTranslateError(String errorMsg) {
                        Toast.makeText(MonthReportActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                setTranslationMode(true);
                updateTranslation(translation);
            }
        }
    }

    private void updateTranslation(MonthReport monthReport) {
        if (!TextUtils.isEmpty(monthReport.getCurrent_month_customer()))
            etCurrentCustomer.setText(monthReport.getCurrent_month_customer());
        if (!TextUtils.isEmpty(monthReport.getCurrent_month_project()))
            etCurrentProject.setText(monthReport.getCurrent_month_project());
        if (!TextUtils.isEmpty(monthReport.getCurrent_month_question()))
            etCurrentQuestion.setText(monthReport.getCurrent_month_question());
        if (!TextUtils.isEmpty(monthReport.getNext_month_plan()))
            etNextPlan.setText(monthReport.getNext_month_plan());
        if (!TextUtils.isEmpty(monthReport.getNext_month_visit_plan()))
            etNextVisit.setText(monthReport.getNext_month_visit_plan());
        if (!TextUtils.isEmpty(monthReport.getNext_month_sales_goal()))
            etNextGoal.setText(monthReport.getNext_month_sales_goal());
        if (!TextUtils.isEmpty(monthReport.getNext_month_guide()))
            etNextGuide.setText(monthReport.getNext_month_guide());
        if (!TextUtils.isEmpty(monthReport.getSale_goal()))
            etSaleGoal.setText(monthReport.getSale_goal());
        if (!TextUtils.isEmpty(monthReport.getCurrent_goal()))
            etCurrentGoal.setText(monthReport.getCurrent_goal());
        if (!TextUtils.isEmpty(monthReport.getImportant_situation()))
            etImportantSituation.setText(monthReport.getImportant_situation());
        if (!TextUtils.isEmpty(monthReport.getPotential_market()))
            etPotentialMarket.setText(monthReport.getPotential_market());
        if (!TextUtils.isEmpty(monthReport.getCompete_strategy()))
            etCompeteStrategy.setText(monthReport.getCompete_strategy());
        if (!TextUtils.isEmpty(monthReport.getResource_investment()))
            etResourceInvestment.setText(monthReport.getResource_investment());
    }

    private void setTranslationMode(Boolean mode) {
        isTranslationMode = mode;
        if (isTranslationMode) {
            btnTranslation.setImageResource(R.mipmap.common_button_translate_eng);
        } else {
            btnTranslation.setImageResource(R.mipmap.common_button_translate_chi);
        }
    }

    void showConfirmSubmitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.submit).setMessage(R.string.month_report_submit_msg)//是否確認提交（提交後將無法再修改該月份月報）
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthReport.setSubmit(true);
                        saveMonthReport();
                    }
                });
        builder.create().show();
    }

    /**
         * 儲存月報
         */
    void saveMonthReport() {
        monthReport.setUser_id(TokenManager.getInstance().getUser().getId());
        monthReport.setCurrent_month_customer(etCurrentCustomer.getText().toString());
        monthReport.setCurrent_month_project(etCurrentProject.getText().toString());
        monthReport.setCurrent_month_question(etCurrentQuestion.getText().toString());
        monthReport.setNext_month_plan(etNextPlan.getText().toString());
        monthReport.setNext_month_visit_plan(etNextVisit.getText().toString());
        monthReport.setNext_month_sales_goal(etNextGoal.getText().toString());
        monthReport.setNext_month_guide(etNextGuide.getText().toString());
        monthReport.setSale_goal(etSaleGoal.getText().toString());
        monthReport.setCurrent_goal(etCurrentGoal.getText().toString());
        monthReport.setImportant_situation(etImportantSituation.getText().toString());
        monthReport.setPotential_market(etPotentialMarket.getText().toString());
        monthReport.setCompete_strategy(etCompeteStrategy.getText().toString());
        monthReport.setResource_investment(etResourceInvestment.getText().toString());
        monthReport.setTime(time);

        DatabaseHelper.getInstance(MonthReportActivity.this).saveMonthReport(monthReport)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable.add(d);
            }

            @Override
            public void onComplete() {
                finish();
            }

            @Override
            public void onError(Throwable e) {
                ErrorHandler.getInstance().setException(MonthReportActivity.this, e);
            }
        });
    }

    /**
         * 隱藏欄位
         */
    void getHiddenField() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField(TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCompanyHiddenField>() {
            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws Exception {

                if (configCompanyHiddenField.getMonthReport_hidden_current_month_customer()) {
                    containerCurrentCustomer.setVisibility(View.GONE);//本月總結-新客戶開發
                }
                if (configCompanyHiddenField.getMonthReport_hidden_current_month_project()) {
                    containerCurrentProject.setVisibility(View.GONE);//本月總結-新項目跟進
                }
                if (configCompanyHiddenField.getMonthReport_hidden_current_month_question()) {
                    containerCurrentQuestion.setVisibility(View.GONE);//本月總結-問題
                }
                if (configCompanyHiddenField.getMonthReport_hidden_next_month_visit_plan()) {
                    containerNextVisit.setVisibility(View.GONE);//下月計劃-活動計劃
                }
                if (configCompanyHiddenField.getMonthReport_hidden_next_month_sales_goal()) {
                    containerNextGoal.setVisibility(View.GONE);//下月計劃-銷售工作目標
                }
                if (configCompanyHiddenField.getMonthReport_hidden_next_month_guide()) {
                    containerNextGuide.setVisibility(View.GONE);//下月計劃-需要的指導
                }
                if (configCompanyHiddenField.getMonthReport_hidden_sale_goal()) {
                    containerSaleGoal.setVisibility(View.GONE);//銷售額目標達成率
                }
                if (configCompanyHiddenField.getMonthReport_hidden_current_goal()) {
                    containerCurrentGoal.setVisibility(View.GONE);//當月目標達成情況
                }
                if (configCompanyHiddenField.getMonthReport_hidden_important_situation()) {
                    containerImportantSituation.setVisibility(View.GONE);//重點行業動態，重點項目或銷售機會狀態變化情況
                }
                if (configCompanyHiddenField.getMonthReport_hidden_potential_market()) {
                    containerPotentialMarket.setVisibility(View.GONE);//潛在目標市場
                }
                if (configCompanyHiddenField.getMonthReport_hidden_compete_strategy()) {
                    containerCompeteStrategy.setVisibility(View.GONE);//競爭與產品策略建議
                }
                if (configCompanyHiddenField.getMonthReport_hidden_resource_investment()) {
                    containerResourceInvestment.setVisibility(View.GONE);//資源投入準備與建議，須協調支持情況
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(MonthReportActivity.this, throwable);
            }
        }));
    }

    boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (containerCurrentCustomer.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etCurrentCustomer.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.current_month_customer), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerCurrentProject.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etCurrentProject.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.current_month_project), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerCurrentQuestion.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etCurrentQuestion.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.current_month_question), Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(etNextPlan.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.next_month_plan), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerNextVisit.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etNextVisit.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.next_month_visit_plan), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerNextGoal.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etNextGoal.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.next_month_sales_goal), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerNextGuide.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etNextGuide.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.next_month_guide), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerSaleGoal.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etSaleGoal.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.month_report_sale_goal), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerCurrentGoal.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etCurrentGoal.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.month_report_current_goal), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerImportantSituation.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etImportantSituation.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.month_report_important_situation), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerPotentialMarket.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etPotentialMarket.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.month_report_potential_market), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerCompeteStrategy.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etCompeteStrategy.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.month_report_compete_strategy), Toast.LENGTH_LONG).show();
            return false;
        } else if (containerResourceInvestment.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etResourceInvestment.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.month_report_resource_investment), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
         * 更新統計資料
         */
    private void updateStatistic() {
        containerTotal.removeAllViews();
        mDisposable.add(DatabaseHelper.getInstance(this).getMonthReportSummary(start, end, viewerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ReportSummary>()
                {
                    @Override
                    public void accept(ReportSummary weekReport) throws Exception {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.weight = 1;
                        for (ReportTotalType type : ReportTotalType.values()) {
                            ItemReportTotal item = new ItemReportTotal(MonthReportActivity.this);
                            item.setReportTotalType(type, weekReport);
                            containerTotal.addView(item, params);
                        }
                    }
                }));
    }

}
