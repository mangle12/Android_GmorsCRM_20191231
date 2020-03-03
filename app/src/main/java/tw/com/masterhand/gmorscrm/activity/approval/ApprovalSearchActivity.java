package tw.com.masterhand.gmorscrm.activity.approval;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnEditorAction;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.tools.GsonGMTDateAdapter;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.ItemApprovalTab;

public class ApprovalSearchActivity extends BaseUserCheckActivity implements TextView
        .OnEditorActionListener, ItemApprovalTab
        .OnApprovalSelectListener {
    final int REQUEST_DETAIL = 200;

    @BindView(R.id.editText_search)
    protected EditText etSearch;// 搜尋輸入框
    @BindView(R.id.container)
    protected LinearLayout container;// 搜尋結果列表

    List<Approval> approvalList;
    Approval selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        etSearch.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onUserChecked() {
        getDataIntent();
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
    private void getDataIntent() {
        gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonGMTDateAdapter())
                .create();
        approvalList = gson.fromJson(getIntent().getStringExtra(MyApplication
                .INTENT_KEY_APPROVAL), new
                TypeToken<List<Approval>>() {
                }.getType());
        performSearch();
    }

    /**
     * 開始搜尋
     */
    protected void performSearch() {
        container.removeAllViews();
        String keyword = etSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            container.addView(getEmptyImageView(null));
            return;
        }
        Logger.e(TAG, "search keyword:" + keyword);
        for (Approval approval : approvalList) {
            if (approval.getName().contains(keyword) || (approval.getCustomer() != null &&
                    approval.getCustomer().getName().contains(keyword))) {
                ItemApprovalTab item = new ItemApprovalTab(this);
                item.setApproval(approval);
                item.setListener(this);
                container.addView(item);
            }
        }
        if (container.getChildCount() == 0)
            container.addView(getEmptyImageView(null));
    }

    @Override
    public void onApprovalSelect(Approval approval) {
        selected = approval;
        Intent intent = null;
        switch (approval.getType()) {
            case ABSENT:
                intent = new Intent(this, ApprovalAbsentActivity.class);
                break;
            case REIMBURSEMENT:
                intent = new Intent(this, ApprovalReimbursementActivity.class);
                break;
            case CONTRACT:
                intent = new Intent(this, ApprovalContractActivity.class);
                break;
            case SAMPLE:
                intent = new Intent(this, ApprovalSampleActivity.class);
                break;
            case QUOTATION:
                intent = new Intent(this, ApprovalQuotationActivity.class);
                break;
        }
        if (intent != null) {
            intent.putExtra(MyApplication.INTENT_KEY_TRIP, approval.getId());
            intent.putExtra(MyApplication.INTENT_KEY_APPROVAL, gson.toJson(approval));
            startActivityForResult(intent, REQUEST_DETAIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_DETAIL:
                    approvalList.remove(selected);
                    selected = null;
                    performSearch();
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
