package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;

public class PeriodSpinner extends LinearLayout {
    Context context;
    @BindView(R.id.spinner_year)
    Spinner spinnerYear;
    @BindView(R.id.spinner_month)
    Spinner spinnerMonth;
    @BindView(R.id.spinner_season)
    Spinner spinnerSeason;

    int year;
    int season;//0:無選擇 1~4:四季
    int month;
    OnPeriodSelectListener listener;

    public interface OnPeriodSelectListener {
        void onPeriodSelected(int year, int season, int month);
    }

    public PeriodSpinner(Context context) {
        super(context);
        init(context);
    }

    public PeriodSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PeriodSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.spinner_period, this);
        ButterKnife.bind(this, view);
        // 篩選
        Calendar today = Calendar.getInstance(Locale.getDefault());
        // 年度篩選
        final ArrayAdapter<String> adapterYear = new ArrayAdapter<>(context, R.layout.spinner);
        String[] years = new String[3];
        year = today.get(Calendar.YEAR);
        for (int i = 0; i < 3; i++) {
            years[i] = String.valueOf(year - i);
        }
        adapterYear.addAll(years);
        adapterYear.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerYear.setAdapter(adapterYear);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                year = Integer.parseInt(adapterYear.getItem(position));
                if (listener != null)
                    listener.onPeriodSelected(year, season, month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // 季度篩選
        final ArrayAdapter<String> adapterSeason = new ArrayAdapter<>(context, R.layout.spinner);
        final String[] seasons = new String[5];
        season = 0;
        seasons[0] = context.getString(R.string.season_unit);
        seasons[1] = context.getString(R.string.season1);
        seasons[2] = context.getString(R.string.season2);
        seasons[3] = context.getString(R.string.season3);
        seasons[4] = context.getString(R.string.season4);
        adapterSeason.addAll(seasons);
        adapterSeason.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerSeason.setAdapter(adapterSeason);
        spinnerSeason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                season = position;
                if (season != 0) {
                    month = 0;
                    spinnerMonth.setSelection(month);
                }
                if (listener != null)
                    listener.onPeriodSelected(year, season, month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // 月份篩選
        final ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(context, R.layout.spinner);
        String[] months = new String[13];
        month = today.get(Calendar.MONTH) + 1;
        months[0] = context.getString(R.string.month_unit);
        for (int i = 1; i < 13; i++) {
            months[i] = String.valueOf(i) + context.getString(R.string.month);
        }
        adapterMonth.addAll(months);
        adapterMonth.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                month = position;
                if (month != 0) {
                    season = 0;
                    spinnerSeason.setSelection(season);
                }
                if (listener != null)
                    listener.onPeriodSelected(year, season, month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerMonth.setSelection(month);
    }

    public void setPeriodSelectListener(OnPeriodSelectListener listener) {
        this.listener = listener;
    }
}
