package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.room.setting.Department;

public class SearchResultSpinner extends LinearLayout {
    Context context;
    @BindView(R.id.spinner_time)
    Spinner spinnerTime;
    @BindView(R.id.spinner_type)
    Spinner spinnerType;
    @BindView(R.id.spinner_department)
    Spinner spinnerDepartment;

    Date start, end;// 時間區間
    TripType type;// null:所有類型
    String departmentId;// null:所有部門
    OnChangeListener listener;

    public interface OnChangeListener {
        void onChange(Date start, Date end, TripType type, String departmentId);
        void onDateDefine();
    }

    public void setOnChangeListener(OnChangeListener listener) {
        this.listener = listener;
    }

    public SearchResultSpinner(Context context) {
        super(context);
        init(context);
    }

    public SearchResultSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchResultSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.spinner_search_result, this);
        ButterKnife.bind(this, view);
        // 時間篩選
        final ArrayAdapter<String> adapterTime = new ArrayAdapter<>(context, R.layout.spinner);
        String[] time = context.getResources().getStringArray(R.array.search_time);
        adapterTime.addAll(time);
        adapterTime.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerTime.setAdapter(adapterTime);
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                switch (position) {
                    case 0:
                        // 今天
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        start = calendar.getTime();
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        end = calendar.getTime();
                        onChange();
                        break;
                    case 1:
                        // 昨天
                        calendar.add(Calendar.DATE, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        start = calendar.getTime();
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        end = calendar.getTime();
                        onChange();
                        break;
                    case 2:
                        // 本周
                        calendar.set(Calendar.DAY_OF_WEEK, 1);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        start = calendar.getTime();
                        calendar.set(Calendar.DAY_OF_WEEK, 7);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        end = calendar.getTime();
                        onChange();
                        break;
                    case 3:
                        // 本月
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        start = calendar.getTime();
                        calendar.add(Calendar.MONTH, 1);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.DATE, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        end = calendar.getTime();
                        onChange();
                        break;
                    case 4:
                        // 自定義
                        if (listener != null)
                            listener.onDateDefine();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // 類型篩選
        final ArrayAdapter<String> adapterType = new ArrayAdapter<>(context, R.layout.spinner);
        final String[] types = new String[TripType.values().length + 1];
        types[0] = context.getString(R.string.all_type);
        for (int i = 0; i < TripType.values().length; i++) {
            types[i + 1] = context.getString(TripType.values()[i].getTitle());
        }
        adapterType.addAll(types);
        adapterType.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerType.setAdapter(adapterType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    type = null;
                    onChange();
                } else {
                    type = TripType.values()[position - 1];
                    onChange();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // 部門篩選
        DatabaseHelper.getInstance(context).getDepartment(TokenManager.getInstance().getUser().getCompany_id()).toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Department>>() {

            @Override
            public void accept(final List<Department> departments) throws Exception {
                final ArrayAdapter<String> adapterDepart = new ArrayAdapter<>(context, R.layout.spinner);
                final String[] departs = new String[departments.size() + 1];
                departs[0] = context.getString(R.string.all_department);
                int i = 1;
                for (Department department : departments) {
                    departs[i] = department.getName();
                    i++;
                }
                adapterDepart.addAll(departs);
                adapterDepart.setDropDownViewResource(R.layout.spinner_dropdown);
                spinnerDepartment.setAdapter(adapterDepart);
                spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        if (position == 0) {
                            departmentId = null;
                            onChange();
                        } else {
                            departmentId = departments.get(position - 1).getId();
                            onChange();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
    }

    void onChange() {
        if (listener != null)
            listener.onChange(start, end, type, departmentId);
    }

    void updateUI() {

    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public TripType getType() {
        return type;
    }

    public String getDepartmentId() {
        return departmentId;
    }
}
