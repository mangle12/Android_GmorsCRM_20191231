package tw.com.masterhand.gmorscrm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCountry;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class LocationSelectActivity extends BaseUserCheckActivity {
    public final static String KEY_COUNTRY = "country";
    public final static String KEY_RESULT = "result";

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.btnSearch)
    ImageButton btnSearch;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.container)
    LinearLayout container;

    private String countryId;
    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);
        init();
    }

    @Override
    protected void onUserChecked() {
        getData();
    }

    private void init() {
        tvTitle.setText(getString(R.string.title_activity_location_select));
    }

    private void getData() {
        countryId = getIntent().getStringExtra(KEY_COUNTRY);
        if (TextUtils.isEmpty(countryId)) {
            selectCountry();
        } else {
            selectCity(countryId);
        }
    }

    void selectCountry() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCountry().observeOn(AndroidSchedulers
                .mainThread()
        ).toList().subscribe(new Consumer<List<ConfigCountry>>() {
            @Override
            public void accept(final List<ConfigCountry> countries) throws Exception {
                updateCountryList(countries);
            }
        }));
    }

    void selectCity(String countryId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCityByCountry(countryId).observeOn(AndroidSchedulers.mainThread())
                .toList().subscribe(new Consumer<List<ConfigCity>>() {
                    @Override
                    public void accept(final List<ConfigCity> cities) throws Exception {
                        updateCityList(cities);
                    }
                }));
    }

    /**
     * 鍵盤點擊搜尋
     */
    @OnEditorAction(R.id.etSearch)
    boolean imeSearch(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            getSearchData();
            return true;
        }
        return false;
    }

    /**
     * 點擊搜尋鈕
     */
    @OnClick(R.id.btnSearch)
    void search() {
        if (etSearch.getVisibility() == View.GONE) {
            etSearch.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            btnBack.setImageResource(R.mipmap.common_close);
        } else {
            getSearchData();
        }
    }

    /**
     * 點擊返回/關閉鈕
     */
    @OnClick(R.id.btnBack)
    @Override
    public void onBackPressed() {
        if (etSearch.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            etSearch.setVisibility(View.GONE);
            keyword = "";
            tvTitle.setVisibility(View.VISIBLE);
            btnBack.setImageResource(R.mipmap.common_left_black);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            getData();
        }
    }

    /**
     * 取得搜尋列表
     */
    void getSearchData() {
        keyword = etSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            container.addView(getEmptyImageView(null));
            return;
        }
        Logger.e(TAG, "search keyword:" + keyword);
        if (TextUtils.isEmpty(countryId)) {
            selectCountry();
        } else {
            selectCity(countryId);
        }
    }

    /**
     * 刷新國家列表
     */
    void updateCountryList(List<ConfigCountry> configs) {
        container.removeAllViews();
        for (final ConfigCountry config : configs) {
            if (config.getName().contains(keyword)) {
                TextView tvItem = generateItem();
                tvItem.setText(config.getName());
                tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_RESULT, gson.toJson(config));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
                container.addView(tvItem, new LinearLayout.LayoutParams
                        (LinearLayout
                                .LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.btn_size)));
            }
        }
        if (container.getChildCount() == 0)
            container.addView(getEmptyImageView(null));
    }

    /**
     * 刷新城市列表
     */
    void updateCityList(List<ConfigCity> configs) {
        container.removeAllViews();
        for (final ConfigCity config : configs) {
            if (config.getName().contains(keyword)) {
                TextView tvItem = generateItem();
                tvItem.setText(config.getName());
                tvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(KEY_RESULT, gson.toJson(config));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
                container.addView(tvItem, new LinearLayout.LayoutParams
                        (LinearLayout
                                .LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.btn_size)));
            }
        }
        if (container.getChildCount() == 0)
            container.addView(getEmptyImageView(null));
    }

    private TextView generateItem() {
        TextView tvItem = new TextView(this);
        tvItem.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light));
        tvItem.setGravity(Gravity.CENTER_VERTICAL);
        tvItem.setPaddingRelative(UnitChanger.dpToPx(16), 0, 0, 0);
        return tvItem;
    }

}
