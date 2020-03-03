package tw.com.masterhand.gmorscrm.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.sale.SaleEditActivity;
import tw.com.masterhand.gmorscrm.activity.task.AbsentDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ContractDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.CreateActivity;
import tw.com.masterhand.gmorscrm.activity.task.ExpressDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.NonStandardInquiryDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.OfficeDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ProductionDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.QuotationDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ReimbursementDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.RepaymentDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SampleDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpecialPriceDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpecialShipDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpringRingInquiryDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.TaskDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.TravelDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.VisitDetailActivity;
import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunity;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemProject;

public class ProjectHistoryActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.itemContainer)
    LinearLayout container;
    @BindView(R.id.itemProject)
    ItemProject itemProject;
    @BindView(R.id.relativeLayout_add)
    RelativeLayout btnAdd;
    @BindView(R.id.tvAdd)
    TextView tvAdd;

    ProjectWithConfig project;
    boolean isHistory;
    boolean isOpportunityCreateEnable = true;
    String projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_history);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getProject(projectId);
        if (isHistory) {
            updateHistory();
        } else {
            updateSale();
        }
    }

    private void init() {
        projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
        if (TextUtils.isEmpty(projectId)) {
            finish();
        }
        itemProject.hideIndex();
        isHistory = getIntent().getBooleanExtra(MyApplication.INTENT_KEY_TYPE, true);
    }

    void updateHistory() {
        isHistory = true;
        container.removeAllViews();
        tvAdd.setText(R.string.new_history);
        mDisposable.add(DatabaseHelper.getInstance(this).getTripByProject(projectId)
                .toSortedList(new Comparator<Trip>() {

                    @Override
                    public int compare(Trip o1, Trip o2) {
                        return o2.getFrom_date().compareTo(o1.getFrom_date());
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Trip>>() {
                    @Override
                    public void accept(List<Trip> trips) throws Exception {
                        if (trips.size() > 0) {
                            for (Trip trip : trips) {
                                generateHistoryItem(trip);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProjectHistoryActivity.this,
                                throwable);
                    }
                }));
    }

    void updateSale() {
        isHistory = false;
        container.removeAllViews();
        tvAdd.setText(R.string.new_sales);
        mDisposable.add(DatabaseHelper.getInstance(this).getSalesOpportunityByProject(projectId)
                .observeOn(AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<SalesOpportunity>>() {

                    @Override
                    public void accept(List<SalesOpportunity> salesOpportunities) throws Exception {
                        Logger.e(TAG, "salesOpportunities.size:" + salesOpportunities.size());
                        if (salesOpportunities.size() > 0) {
                            for (SalesOpportunity opportunity : salesOpportunities) {
                                generateSalesItem(opportunity);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProjectHistoryActivity.this,
                                throwable);
                    }
                }));
    }

    private void generateSalesItem(final SalesOpportunity opportunity) {
        View view = getLayoutInflater().inflate(R.layout.item_project_history, container,
                false);
        TextView tvTime = view.findViewById(R.id.textView_time);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        final TextView tvSubtitle = view.findViewById(R.id.textView_subtitle);
        TextView topBar = view.findViewById(R.id.topBar);
        if (opportunity.getPercentage() < 0) {
            isOpportunityCreateEnable = false;
            topBar.setBackgroundColor(ContextCompat.getColor(ProjectHistoryActivity.this,
                    ProjectStatus.LOSE.getColor()));
        } else if (opportunity.getPercentage() >= 100) {
            isOpportunityCreateEnable = false;
            topBar.setBackgroundColor(ContextCompat.getColor(ProjectHistoryActivity.this,
                    ProjectStatus.WIN.getColor()));
        } else {
            topBar.setBackgroundColor(ContextCompat.getColor(ProjectHistoryActivity.this,
                    R.color.orange));
        }
        int showPercent = opportunity.getPercentage() < 0 ? 0 : opportunity.getPercentage();

        topBar.setText(ProjectStatus.getName(this, opportunity.getStage(), opportunity
                .getDepartment_sales_opportunity()) + " " + showPercent + "%");
        tvTime.setText(TimeFormater.getInstance().toDateTimeFormat(opportunity.getCreated_at()));
        tvTitle.setText(opportunity.getName());
        mDisposable.add(DatabaseHelper.getInstance(ProjectHistoryActivity.this).getUserById
                (opportunity
                        .getUser_id()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                if (user != null)
                    tvSubtitle.setText(getString(R.string.builder) + " " + user
                            .getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ProjectHistoryActivity.this,
                        throwable);
            }
        }));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectHistoryActivity.this, SaleEditActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_ID, opportunity.getId());
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, projectId);
                startActivity(intent);
            }
        });
        container.addView(view);
    }

    private void generateHistoryItem(final Trip trip) {
        View view = getLayoutInflater().inflate(R.layout.item_project_history, container,
                false);
        TextView tvTime = view.findViewById(R.id.textView_time);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        final TextView tvSubtitle = view.findViewById(R.id.textView_subtitle);
        TextView topBar = view.findViewById(R.id.topBar);
        topBar.setBackgroundColor(ContextCompat.getColor(ProjectHistoryActivity.this,
                trip.getType().getColor()));
        topBar.setCompoundDrawablesRelativeWithIntrinsicBounds(trip.getType()
                .getIcon(), 0, 0, 0);
        topBar.setText(trip.getType().getTitle());
        if (trip.getDate_type()) {
            tvTime.setText(TimeFormater.getInstance().toDateFormat(trip.getFrom_date()));
        } else {
            tvTime.setText(TimeFormater.getInstance().toDateTimeFormat(trip.getFrom_date()));
        }
        tvTitle.setText(trip.getName());
        if (trip.getUser_id() != null)
            mDisposable.add(DatabaseHelper.getInstance(ProjectHistoryActivity.this).getUserById(trip
                    .getUser_id()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

                @Override
                public void accept(User user) throws Exception {
                    if (user != null)
                        tvSubtitle.setText(getString(R.string.builder) + " " + user
                                .getShowName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(ProjectHistoryActivity.this,
                            throwable);
                }
            }));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                String parentId = "";
                switch (trip.getType()) {
                    case VISIT:
                        intent = new Intent(ProjectHistoryActivity.this,
                                VisitDetailActivity.class);
                        break;
                    case OFFICE:
                        intent = new Intent(ProjectHistoryActivity.this,
                                OfficeDetailActivity.class);
                        break;
                    case TASK:
                        intent = new Intent(ProjectHistoryActivity.this,
                                TaskDetailActivity.class);
                        break;
                    case ABSENT:
                        intent = new Intent(ProjectHistoryActivity.this,
                                AbsentDetailActivity.class);
                        break;
                    case QUOTATION:
                        intent = new Intent(ProjectHistoryActivity.this,
                                QuotationDetailActivity.class);
                        break;
                    case CONTRACT:
                        intent = new Intent(ProjectHistoryActivity.this,
                                ContractDetailActivity.class);
                        break;
                    case REIMBURSEMENT:
                        intent = new Intent(ProjectHistoryActivity.this,
                                ReimbursementDetailActivity.class);
                        break;
                    case SAMPLE:
                        intent = new Intent(ProjectHistoryActivity.this,
                                SampleDetailActivity.class);
                        break;
                    case SPECIAL_PRICE:
                        intent = new Intent(ProjectHistoryActivity.this,
                                SpecialPriceDetailActivity.class);
                        break;
                    case PRODUCTION:
                        intent = new Intent(ProjectHistoryActivity.this, ProductionDetailActivity
                                .class);
                        break;
                    case NON_STANDARD_INQUIRY:
                        intent = new Intent(ProjectHistoryActivity.this,
                                NonStandardInquiryDetailActivity.class);
                        break;
                    case SPRING_RING_INQUIRY:
                        intent = new Intent(ProjectHistoryActivity.this,
                                SpringRingInquiryDetailActivity.class);
                        break;
                    case SPECIAL_SHIP:
                        intent = new Intent(ProjectHistoryActivity.this,
                                SpecialShipDetailActivity.class);
                        break;
                    case REPAYMENT:
                        intent = new Intent(ProjectHistoryActivity.this, RepaymentDetailActivity
                                .class);
                        break;
                    case EXPRESS:
                        intent = new Intent(ProjectHistoryActivity.this, ExpressDetailActivity
                                .class);
                        break;
                    case TRAVEL:
                        intent = new Intent(ProjectHistoryActivity.this, TravelDetailActivity
                                .class);
                        break;
                }
                intent.putExtra(MyApplication.INTENT_KEY_TRIP, trip.getId
                        ());
                startActivity(intent);
            }
        });
        container.addView(view);
    }

    private void getProject(String projectId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId, TokenManager
                .getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProjectWithConfig>() {
                    @Override
                    public void accept(ProjectWithConfig result) throws Exception {
                        project = result;
                        appbar.setTitle(project.getCustomer().getName());
                        itemProject.setProject(project, new ItemProject.OnSelectListener() {
                            @Override
                            public void onHistorySelected() {
                                updateHistory();
                            }

                            @Override
                            public void onSaleSelected() {
                                updateSale();
                            }
                        }, isHistory);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProjectHistoryActivity.this,
                                throwable);
                    }
                }));
    }

    /**
     * 新增歷程
     */
    @OnClick(R.id.relativeLayout_add)
    void addHistory(View view) {
        Intent intent = null;
        if (isHistory) {
            intent = new Intent(this, CreateActivity.class);
            intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, project.getCustomer().getId());
            intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getProject().getId());
            startActivityForResult(intent, MyApplication.REQUEST_ADD_WORK);
        } else {
            if (isOpportunityCreateEnable) {
                intent = new Intent(this, SaleEditActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getProject().getId());
                startActivityForResult(intent, MyApplication.REQUEST_ADD_OPPORTUNITY);
            } else {
                Toast.makeText(this, R.string.error_msg_opportunity_create_disable, Toast
                        .LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_ADD_OPPORTUNITY:
                    getProject(projectId);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
