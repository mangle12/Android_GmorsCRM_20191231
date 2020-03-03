package tw.com.masterhand.gmorscrm.activity.sale;

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

import butterknife.BindView;
import butterknife.OnEditorAction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.adapter.SaleListAdapter;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class SaleSearchActivity extends BaseUserCheckActivity {
    @BindView(R.id.editText_search)
    protected EditText etSearch;// 搜尋輸入框
    @BindView(R.id.container)
    protected RecyclerView container;// 搜尋結果列表

    String viewer;
    SaleListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etSearch.setVisibility(View.VISIBLE);
        etSearch.setHint(R.string.hint_sale_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        container.setLayoutManager(layoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable
                .divider_transparent_v_8dp);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        container.addItemDecoration(dividerItemDecoration);
        container.addItemDecoration(new StartOffsetItemDecoration(UnitChanger.dpToPx(8)));
        container.addItemDecoration(new EndOffsetItemDecoration(UnitChanger.dpToPx(8)));
        adapter = new SaleListAdapter(this);
        container.setAdapter(adapter);
    }

    @Override
    protected void onUserChecked() {
        viewer = TokenManager.getInstance().getUser().getId();
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

    protected void performSearch() {
        adapter.clear();
        String keyword = etSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
//            Toast.makeText(this, getString(R.string.error_search_empty), Toast.LENGTH_SHORT)
// .show();
//            container.addView(getEmptyImageView(null));
            return;
        }
        keyword = "%" + keyword + "%";
        Logger.e(TAG, "search keyword:" + keyword);
        mDisposable.add(DatabaseHelper.getInstance(this).searchProject(TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                (), keyword)
                .filter(new Predicate<ProjectWithConfig>() {
                    @Override
                    public boolean test(@NonNull ProjectWithConfig projectWithConfig) throws
                            Exception {
                        return !TextUtils.isEmpty(projectWithConfig.getSalesOpportunity().getId());
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

                    @Override
                    public void accept(ProjectWithConfig project) throws Exception {
                        adapter.addProject(project);
                    }
                }));
    }
}
