package tw.com.masterhand.gmorscrm.activity.customer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.dgreenhalgh.android.simpleitemdecoration.linear.EndOffsetItemDecoration;
import com.dgreenhalgh.android.simpleitemdecoration.linear.StartOffsetItemDecoration;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.OnEditorAction;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.adapter.CustomerListAdapter;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class CustomerSearchActivity extends BaseUserCheckActivity {
    @BindView(R.id.editText_search)
    protected EditText etSearch;// 搜尋輸入框
    @BindView(R.id.container)
    protected RecyclerView container;// 搜尋結果列表


    String viewer;
    CustomerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etSearch.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        container.setLayoutManager(layoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable
                .divider_transparent_v_8dp);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        container.addItemDecoration(dividerItemDecoration);
        container.addItemDecoration(new StartOffsetItemDecoration(UnitChanger.dpToPx(8)));
        container.addItemDecoration(new EndOffsetItemDecoration(UnitChanger.dpToPx(8)));
        adapter = new CustomerListAdapter(this);
        container.setAdapter(adapter);
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
        performSearch();
    }

    protected void performSearch() {
        adapter.clear();
        String keyword = etSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            adapter.showEmpty();
            return;
        }
        keyword = "%" + keyword + "%";
        Logger.e(TAG, "search keyword:" + keyword);
        DatabaseHelper.getInstance(this).searchCustomer(keyword, TokenManager.getInstance()
                .getUser().getId())
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new FlowableSubscriber<Customer>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Customer customer) {
                adapter.addCustomer(customer);
            }

            @Override
            public void onError(Throwable t) {
                adapter.showEmpty();
            }

            @Override
            public void onComplete() {
                if (adapter.getItemCount() == 0)
                    adapter.showEmpty();
            }
        });
    }
}
