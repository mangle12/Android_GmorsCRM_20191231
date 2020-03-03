package tw.com.masterhand.gmorscrm.activity.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.task.CreateActivity;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.enums.ReportFilterType;
import tw.com.masterhand.gmorscrm.enums.ReportTotalType;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.model.ReportSummary;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemMain;
import tw.com.masterhand.gmorscrm.view.ItemReport;
import tw.com.masterhand.gmorscrm.view.ItemReportTotal;

public class WeekReportActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.itemReport)
    ItemReport itemReport;
    @BindView(R.id.imageButton_left)
    ImageButton btnLeft;
    @BindView(R.id.imageButton_right)
    ImageButton btnRight;
    @BindView(R.id.container_total)
    LinearLayout containerTotal;
    //    @BindView(R.id.container_customer)
//    LinearLayout containerCustomer;
    @BindView(R.id.container_task)
    LinearLayout containerTask;

    Calendar date;
    String parentId;
    ReportFilterType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_report);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateList();
    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_week_report));//週計畫
        String userId = getIntent().getStringExtra(MyApplication.INTENT_KEY_USER);
        String departmentId = getIntent().getStringExtra(MyApplication.INTENT_KEY_DEPARTMENT);
        String companyId = getIntent().getStringExtra(MyApplication.INTENT_KEY_COMPANY);

        if (!TextUtils.isEmpty(userId)) {
            type = ReportFilterType.PERSON;
            parentId = userId;
        }
        if (!TextUtils.isEmpty(departmentId)) {
            type = ReportFilterType.DEPARTMENT;
            parentId = departmentId;
        }
        if (!TextUtils.isEmpty(companyId)) {
            type = ReportFilterType.COMPANY;
            parentId = companyId;
        }
        if (companyId != null && companyId.equals("")) {
            type = ReportFilterType.COMPANY;
            parentId = companyId;
        }
        if (TextUtils.isEmpty(parentId) && type != ReportFilterType.COMPANY) {
            finish();
            return;
        }

        date = Calendar.getInstance(Locale.getDefault());
        date.setTimeInMillis(getIntent().getLongExtra(MyApplication.INTENT_KEY_DATE, new Date().getTime()));
        date.set(Calendar.DAY_OF_WEEK, 1);
    }

    private void updateList() {
        switch (type) {
            case PERSON: {
                itemReport.setUserId(parentId);
                break;
            }
            case DEPARTMENT: {
                itemReport.setDepartmentId(parentId);
                break;
            }
            case COMPANY: {
                itemReport.setCompanyId(parentId);
                break;
            }
        }
        itemReport.setTime(date, mDisposable);

        // 統計資訊
        updateStatistic();

        // 任務列表
        updateTripList();
    }

    /**
         * 統計資訊
         */
    private void updateStatistic() {
        startProgressDialog();
        containerTotal.removeAllViews();

        //取得週計畫
        mDisposable.add(DatabaseHelper.getInstance(this).getWeekReport(date.getTime(), parentId, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ReportSummary>() {

                    @Override
                    public void accept(ReportSummary weekReport) throws Exception {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.weight = 1;
                        for (ReportTotalType type : ReportTotalType.values()) {
                            ItemReportTotal item = new ItemReportTotal(WeekReportActivity.this);
                            item.setReportTotalType(type, weekReport);
                            containerTotal.addView(item, params);
                        }
                        stopProgressDialog();
                    }
                }));
    }

    /**
         * 任務列表
         */
    private void updateTripList() {
        containerTask.removeAllViews();
        Calendar tempDate = (Calendar) date.clone();

        for (int i = 0; i < 7; i++) {
            final Date sqlDate = tempDate.getTime();
            mDisposable.add(DatabaseHelper.getInstance(this).getMainTrip(sqlDate, parentId, type)
                    .observeOn(AndroidSchedulers.mainThread()).toList()
                    .subscribe(new Consumer<List<MainTrip>>() {
                        @Override
                        public void accept(List<MainTrip> mainTrips) throws Exception {
                            View item = getLayoutInflater().inflate(R.layout.item_list, containerTask, false);
                            item.setBackgroundColor(ContextCompat.getColor(WeekReportActivity.this, R.color.colorPrimary));
                            TextView tvTitle = item.findViewById(R.id.textView_title);
                            TextView tvCount = item.findViewById(R.id.textView_count);
                            ImageView ivIcon = item.findViewById(R.id.imageView_icon);
                            ImageView ivAdd = item.findViewById(R.id.imageView_next);
                            tvTitle.setText(TimeFormater.getInstance().toMonthDayFormat(sqlDate) + " " + TimeFormater.getInstance().toWeekFormat(sqlDate));
                            tvCount.setText(mainTrips.size() + getString(R.string.task_unit));// n項工作
                            ivIcon.setVisibility(View.GONE);
                            ivAdd.setImageResource(R.mipmap.common_add);

                            ivAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 點擊新增任務
                                    Intent intent = new Intent(WeekReportActivity.this, CreateActivity.class);
                                    startActivity(intent);
                                }
                            });
                            containerTask.addView(item);

                            int padding = UnitChanger.dpToPx(8);
                            if (mainTrips.size() > 0) {
                                LinearLayout container = new LinearLayout(WeekReportActivity.this);
                                container.setOrientation(LinearLayout.VERTICAL);
                                container.setPadding(padding, padding, padding, padding);
                                container.setDividerDrawable(ContextCompat.getDrawable(WeekReportActivity.this, R.drawable.divider_transparent_v_8dp));
                                container.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                                for (MainTrip mainTrip : mainTrips)
                                {
                                    ItemMain itemMain = generateItem(mainTrip);
                                    if (itemMain != null)
                                    {
                                        container.addView(itemMain);
                                    }
                                }
                                containerTask.addView(container);
                            } else {
                                TextView tvEmpty = new TextView(WeekReportActivity.this);
                                tvEmpty.setText(getString(R.string.msg_no_task));//目前未預定工作
                                tvEmpty.setGravity(Gravity.CENTER);
                                tvEmpty.setPadding(0, padding, 0, padding);
                                containerTask.addView(tvEmpty);
                            }
                        }
                    }));
            tempDate.add(Calendar.DATE, 1);
        }
    }

    private ItemMain generateItem(MainTrip mainTrip) {
        List<Participant> participants = mainTrip.getParticipants();
        Participant participant = null;// user
        String userId = TokenManager.getInstance().getUser().getId();
        for (Participant item : participants) {
            if (item.getUser_id().equals(userId)) {
                participant = item;
                break;
            }
        }
        if (mainTrip.getTrip().getStatus() == TripStatus.CANCEL) {
            ItemMain itemMain = new ItemMain(WeekReportActivity.this);
            itemMain.setDate(date.getTime());
            itemMain.setTrip(mainTrip);
            return itemMain;
        } else if (participant == null || participant.getAccept() != AcceptType.NO) {
            ItemMain itemMain = new ItemMain(WeekReportActivity.this);
            itemMain.setDate(date.getTime());
            itemMain.setTrip(mainTrip);
            return itemMain;
        }
        return null;
    }

    @OnClick(R.id.imageButton_left)
    void moveDatePrevious() {
        date.add(Calendar.DATE, -7);
        updateList();
    }

    @OnClick(R.id.imageButton_right)
    void moveDateNext() {
        date.add(Calendar.DATE, 7);
        updateList();
    }

}
