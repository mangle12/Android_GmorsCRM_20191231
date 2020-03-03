package tw.com.masterhand.gmorscrm.activity.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ReportFilterType;
import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.FilterCompany;
import tw.com.masterhand.gmorscrm.view.FilterDepartment;
import tw.com.masterhand.gmorscrm.view.FilterPersonal;
import tw.com.masterhand.gmorscrm.view.ItemReport;

public class ReportActivity extends BaseUserCheckActivity implements FilterPersonal.OnSelectedListener, FilterDepartment.OnSelectedListener, FilterCompany.OnSelectedListener {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.filterPersonal)
    FilterPersonal filterPersonal;
    @BindView(R.id.filterDepartment)
    FilterDepartment filterDepartment;
    @BindView(R.id.filterCompany)
    FilterCompany filterCompany;
    @BindView(R.id.containerFilter)
    LinearLayout containerFilter;
    @BindView(R.id.containerFilterItem)
    LinearLayout containerFilterItem;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.spinner_year)
    Spinner spinnerYear;
    @BindView(R.id.spinner_month)
    Spinner spinnerMonth;
    @BindView(R.id.btnMonthReport)
    Button btnMonthReport;

    int year, month;
    boolean isInit = false;
    Date monthStart, monthEnd;

    ReportFilterType filterType = ReportFilterType.PERSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (!isInit)
            init();
    }

    private void init() {
        isInit = true;

        /*篩選*/
        appbar.addFunction(getString(R.string.filter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerFilter.getVisibility() == View.VISIBLE) {
                    containerFilter.setVisibility(View.GONE);
                    updateList();
                } else
                    containerFilter.setVisibility(View.VISIBLE);
            }
        });

        filterPersonal.setSelected(TokenManager.getInstance().getUser());//個人查看範圍
        filterPersonal.setListener(this);//個人查看範圍
        filterDepartment.setListener(this);//部門查看範圍
        filterCompany.setListener(this);//通路查看範圍

        // 年度選擇
        final ArrayAdapter<String> adapterYear = new ArrayAdapter<>(this, R.layout.spinner);
        String[] years = new String[3];
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        year = calendar.get(Calendar.YEAR);
        calendar.add(Calendar.YEAR, -1);
        for (int i = 0; i < years.length; i++) {
            years[i] = String.valueOf(calendar.get(Calendar.YEAR));
            calendar.add(Calendar.YEAR, 1);
        }
        adapterYear.addAll(years);
        adapterYear.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerYear.setAdapter(adapterYear);
        spinnerYear.setSelection(1);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                year = Integer.parseInt(adapterYear.getItem(position));
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 月份選擇
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(this, R.layout.spinner);
        String[] months = new String[12];
        month = 0;
        for (int i = 0; i < months.length; i++) {
            if (i == calendar.get(Calendar.MONTH)) {
                month = i;
            }
            months[i] = String.valueOf(i + 1) + getString(R.string.month);
        }

        adapterMonth.addAll(months);
        adapterMonth.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setSelection(month);
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                month = position;
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
         * 刷新列表
         */
    private void updateList() {
        updateCondition();

        container.removeAllViews();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1 * (calendar.get(Calendar.DAY_OF_WEEK) - 1));
        Logger.e(TAG, "start date:" + TimeFormater.getInstance().toDateTimeFormat(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_WEEK, 1);

        for (int i = 0; i < 6; i++) {// 一個月不會超過六週
            final Date startOfWeek = calendar.getTime();
            String parentId = null;
            switch (filterType) {
                case PERSON: {
                    parentId = filterPersonal.getSelected().getId();
                    break;
                }
                case DEPARTMENT: {
                    parentId = filterDepartment.getSelected().getId();
                    break;
                }
                case COMPANY: {
                    if (filterDepartment.isSelectAll()) {
                        parentId = TokenManager.getInstance().getUser().getCompany_id();
                    } else {
                        if (filterCompany.isSelectAll()) {
                            parentId = "";
                        } else {
                            parentId = filterCompany.getSelected().getId();
                        }
                    }
                    break;
                }
            }

            ItemReport item = new ItemReport(this, parentId, filterType);
            item.setTime(calendar, mDisposable);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ReportActivity.this, WeekReportActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_DATE, startOfWeek.getTime());
                    switch (filterType) {
                        case PERSON: {
                            intent.putExtra(MyApplication.INTENT_KEY_USER, filterPersonal.getSelected().id);
                            break;
                        }
                        case DEPARTMENT: {
                            intent.putExtra(MyApplication.INTENT_KEY_DEPARTMENT, filterDepartment.getSelected().id);
                            break;
                        }
                        case COMPANY: {
                            if (filterDepartment.isSelectAll()) {
                                intent.putExtra(MyApplication.INTENT_KEY_COMPANY, TokenManager.getInstance().getUser().getCompany_id());
                            } else {
                                if (filterCompany.isSelectAll()) {
                                    intent.putExtra(MyApplication.INTENT_KEY_COMPANY, "");
                                } else {
                                    intent.putExtra(MyApplication.INTENT_KEY_COMPANY, filterCompany.getSelected().getId());
                                }
                            }

                            break;
                        }
                    }
                    startActivityForResult(intent, MyApplication.REQUEST_EDIT);
                }
            });
            container.addView(item);
            calendar.add(Calendar.DATE, 7);
//            Logger.e(TAG, "date:" + TimeFormater.toMonthDayFormat(calendar.getTime()));
            if (i > 2) {
//                Logger.e(TAG, "month:" + month + ",cal month:" + calendar.get(Calendar.MONTH));
                if (calendar.get(Calendar.MONTH) != month) {
                    calendar.add(Calendar.DATE, -7);
                    break;
                }
            }
        }

        Calendar monthCalendar = Calendar.getInstance(Locale.getDefault());
        monthCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        monthCalendar.set(Calendar.YEAR, year);
        monthCalendar.set(Calendar.MONTH, month);
        monthCalendar.set(Calendar.HOUR_OF_DAY, 0);
        monthCalendar.set(Calendar.MINUTE, 0);
        monthCalendar.set(Calendar.SECOND, 0);
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        monthStart = monthCalendar.getTime();
        monthCalendar.add(Calendar.MONTH, 1);
        monthCalendar.add(Calendar.DATE, -1);
        monthEnd = monthCalendar.getTime();

        //月報告
        btnMonthReport.setText(getString(R.string.month_report) + " " + TimeFormater
                .getInstance().toMonthDayFormat(monthStart) + "~" +
                TimeFormater.getInstance().toMonthDayFormat(monthEnd));
    }

    //提交月報告
    @OnClick(R.id.btnMonthReport)
    void showMonthReport() {
        Intent intent = new Intent(this, MonthReportActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_DATE_START, TimeFormater.getInstance().toDatabaseFormat(monthStart));
        intent.putExtra(MyApplication.INTENT_KEY_DATE_END, TimeFormater.getInstance().toDatabaseFormat(monthEnd));
        intent.putExtra(MyApplication.INTENT_KEY_USER, filterPersonal.getSelected().id);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        filterPersonal.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MyApplication.REQUEST_EDIT:
                updateList();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //完成
    @OnClick(R.id.btnComplete)
    void filterComplete() {
        containerFilter.setVisibility(View.GONE);
        updateList();
    }

    //全部部門
    @Override
    public void onSelectedAllDepartment() {
        filterCompany.clear();
        filterPersonal.clear();
    }

    //全部通路
    @Override
    public void onSelectedAllCompany() {
        filterDepartment.clear();
        filterPersonal.clear();
    }

    @Override
    public void onSelected(Company company) {
        if (company != null) {
            filterDepartment.clear();
            filterPersonal.clear();
        }
    }

    @Override
    public void onSelected(Department department) {
        if (department != null) {
            filterCompany.clear();
            filterPersonal.clear();
        }
    }

    @Override
    public void onSelected(User user) {
        filterCompany.clear();
        filterDepartment.clear();
    }

    /**
         * 標題Title
         */
    void updateCondition() {
        String condition = getString(R.string.report) + "\n";
        if (filterPersonal.getSelected() != null) {
            filterType = ReportFilterType.PERSON;
            condition += filterPersonal.getSelected().getShowName();
            btnMonthReport.setVisibility(View.VISIBLE);
        }
        if (filterDepartment.getSelected() != null) {
            filterType = ReportFilterType.DEPARTMENT;
            condition += filterDepartment.getSelected().getName();
            btnMonthReport.setVisibility(View.GONE);
        }
        if (filterDepartment.isSelectAll()) {
            filterType = ReportFilterType.COMPANY;
            condition += getString(R.string.all_department);
            btnMonthReport.setVisibility(View.GONE);
        }
        if (filterCompany.getSelected() != null) {
            filterType = ReportFilterType.COMPANY;
            condition += filterCompany.getSelected().getName();
            btnMonthReport.setVisibility(View.GONE);
        }
        if (filterCompany.isSelectAll()) {
            filterType = ReportFilterType.COMPANY;
            condition += getString(R.string.all_company);
            btnMonthReport.setVisibility(View.GONE);
        }
        appbar.setTitle(condition);
    }
}
