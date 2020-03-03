package tw.com.masterhand.gmorscrm;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.reactivestreams.Subscription;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnEditorAction;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.ItemMain;
import tw.com.masterhand.gmorscrm.view.SearchResultSpinner;

public class SearchResultActivity extends BaseUserCheckActivity implements TextView
        .OnEditorActionListener, SearchResultSpinner.OnChangeListener {
    @BindView(R.id.editText_search)
    protected EditText etSearch;// 搜尋輸入框
    @BindView(R.id.container)
    protected LinearLayout container;// 搜尋結果列表
    @BindView(R.id.searchResultSpinner)
    protected SearchResultSpinner searchResultSpinner;// 篩選功能

    String viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        etSearch.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onUserChecked() {
        viewer = TokenManager.getInstance().getUser().getId();
        getSearchIntent();
    }

    /**
     * 點擊鍵盤搜尋鈕
     */
    @OnEditorAction(R.id.editText_search)
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch();
            return true;
        }
        return false;
    }

    /**
     * 取得搜尋關鍵字
     */
    private void getSearchIntent() {
        String keyword = getIntent().getStringExtra(MyApplication.INTENT_KEY_KEYWORD);
        if (!TextUtils.isEmpty(keyword)) {
            etSearch.setText(keyword);
            etSearch.setSelection(etSearch.getText().length());
        }
        etSearch.postDelayed(new Runnable() {
            @Override
            public void run() {
                performSearch();
            }
        }, 1000);
    }

    /**
     * 開始搜尋
     */
    protected void performSearch() {
        searchResultSpinner.setOnChangeListener(this);
        container.removeAllViews();
        String keyword = etSearch.getText().toString();
        keyword = "%" + keyword + "%";
        Logger.e(TAG, "search keyword:" + keyword);
        String departmentId = searchResultSpinner.getDepartmentId();
        TripType type = searchResultSpinner.getType();
        Date start = searchResultSpinner.getStart();
        Date end = searchResultSpinner.getEnd();

        DatabaseHelper.getInstance(this).searchTrip(keyword, type, start, end, departmentId)
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new FlowableSubscriber<MainTrip>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(MainTrip mainTrip) {
                if (mainTrip.getTrip().getUser_id() == null)
                    return;
                ItemMain itemMain = new ItemMain(SearchResultActivity.this);
                itemMain.setDate(new Date(0));
                itemMain.setTrip(mainTrip);
                itemMain.showDate(mainTrip.getTrip().getFrom_date());
                container.addView(itemMain);
            }

            @Override
            public void onError(Throwable t) {
                ErrorHandler.getInstance().setException(SearchResultActivity.this, t);
                container.addView(getEmptyImageView(null));
            }

            @Override
            public void onComplete() {
                if (container.getChildCount() == 0) {
                    container.addView(getEmptyImageView(null));
                }
            }
        });
    }

    @Override
    public void onChange(Date start, Date end, TripType type, String departmentId) {
        performSearch();
    }

    @Override
    public void onDateDefine() {
        Intent intent = new Intent(this, PeriodSelectActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_PERIOD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_SELECT_PERIOD:
                    try {
                        Date start = TimeFormater.getInstance().fromDatabaseFormat(data
                                .getStringExtra
                                (MyApplication.INTENT_KEY_DATE_START));
                        Date end = TimeFormater.getInstance().fromDatabaseFormat(data.getStringExtra
                                (MyApplication.INTENT_KEY_DATE_END));
                        searchResultSpinner.setStart(start);
                        searchResultSpinner.setEnd(end);
                        performSearch();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
