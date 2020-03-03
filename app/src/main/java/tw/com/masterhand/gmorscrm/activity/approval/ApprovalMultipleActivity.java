package tw.com.masterhand.gmorscrm.activity.approval;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.model.Approve;
import tw.com.masterhand.gmorscrm.model.ApproveMulti;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.ItemApprovalMultiple;

public class ApprovalMultipleActivity extends BaseUserCheckActivity implements TextView
        .OnEditorActionListener, ItemApprovalMultiple
        .OnApprovalSelectListener {

    @BindView(R.id.editText_search)
    protected EditText etSearch;// 搜尋輸入框
    @BindView(R.id.container)
    protected LinearLayout container;// 搜尋結果列表

    List<Approval> approvalList;
    List<Approval> selectedList;

    boolean isChange = false;
    boolean isAgree = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_multiple);
        etSearch.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onUserChecked() {
        getDataIntent();
    }

    @Override
    public void onBackPressed() {
        if (isChange) {
            Intent intent = new Intent();
            intent.putExtra(MyApplication.INTENT_KEY_APPROVAL, gson.toJson(approvalList));
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
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
        String data = getIntent().getStringExtra(MyApplication
                .INTENT_KEY_APPROVAL);
        Logger.e(TAG, "getDataIntent:" + data);
        approvalList = gson.fromJson(data, new
                TypeToken<List<Approval>>() {
                }.getType());
        performSearch();
    }

    /**
     * 開始搜尋
     */
    protected void performSearch() {
        selectedList = new ArrayList<>();
        container.removeAllViews();
        String keyword = etSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            for (Approval approval : approvalList) {
                generateItem(approval);
            }
            return;
        }
        Logger.e(TAG, "search keyword:" + keyword);
        for (Approval approval : approvalList) {
            if (approval.getName().contains(keyword) || (approval.getCustomer() != null &&
                    approval.getCustomer().getName().contains(keyword))) {
                generateItem(approval);
            }
        }
        if (container.getChildCount() == 0)
            container.addView(getEmptyImageView(null));
    }

    void generateItem(Approval approval) {
        if (approval.getApproval() != ApprovalStatus.AGREE && approval.getApproval() !=
                ApprovalStatus.DISAGREE) {
            ItemApprovalMultiple item = new ItemApprovalMultiple(this);
            item.setApproval(approval);
            item.setListener(this);
            container.addView(item);
        }
    }

    @Override
    public void onApprovalSelect(Approval approval, boolean isSelected) {
        if (isSelected) {
            selectedList.add(approval);
        } else {
            selectedList.remove(approval);
        }
    }

    @OnClick(R.id.btnAgree)
    void agree() {
        if (!check())
            return;
        isAgree = true;
        showDialog(ApprovalStatus.AGREE);
    }

    @OnClick(R.id.btnDisagree)
    void disagree() {
        if (!check())
            return;
        isAgree = false;
        showDialog(ApprovalStatus.DISAGREE);
    }

    private void showDialog(final ApprovalStatus status) {
        final Dialog dialog = new Dialog(this, MyApplication.DIALOG_STYLE);
        View v = getLayoutInflater().inflate(R.layout.fragment_approval, container, false);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        final EditText etReason = v.findViewById(R.id.etReason);
        v.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        v.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApproveMulti approveMulti = new ApproveMulti();
                String reason = etReason.getText().toString();
                List<Approve> data = new ArrayList<>();
                for (Approval item : selectedList) {
                    data.add(approvalTransfer(item, reason, status));
                }
                approveMulti.setReason(reason);
                approveMulti.setApproval(status);
                approveMulti.setContent(gson.toJson(data));
                dialog.dismiss();
                approve(approveMulti);
            }
        });
        String title = getString(R.string.approval);
        if (status == ApprovalStatus.AGREE) {
            title += getString(R.string.agree);
        } else {
            title += getString(R.string.disagree);
        }
        tvTitle.setText(title);
        dialog.setContentView(v);
        dialog.show();
    }

    private void approve(ApproveMulti approve) {
        Logger.e(TAG, "send approve:" + new Gson().toJson(approve));

        Call<JSONArray> call = ApiHelper.getInstance().getApprovalApi().executeApprove
                (TokenManager.getInstance().getToken(),
                        approve);
        call.enqueue(new Callback<JSONArray>() {
            @Override
            public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
                switch (response.code()) {
                    case 200:
                        isChange = true;
                        for (Iterator<Approval> iter = approvalList.listIterator(); iter.hasNext
                                (); ) {
                            Approval approval = iter.next();
                            if (isSelected(approval)) {
                                iter.remove();
                            }
                        }
                        for (Approval approval : selectedList) {
                            ApprovalStatus status;
                            if (isAgree) {
                                status = ApprovalStatus.AGREE;
                            } else {
                                status = ApprovalStatus.DISAGREE;
                            }
                            for (Approve user : approval.getTrip_approvals()) {
                                if (TextUtils.isEmpty(user.getUser_id())) {
                                    user.setUser_id(TokenManager.getInstance().getUser().getId());
                                    user.setApproval(status);
                                    break;
                                }
                            }
                            if (isAgree)
                                for (Approve user : approval.getTrip_approvals()) {
                                    if (TextUtils.isEmpty(user.getUser_id())) {
                                        status = ApprovalStatus.PROCESS;
                                        break;
                                    }
                                }
                            approval.setApproval(status);
                            approval.setUpdated_at(new Date());
                        }
                        approvalList.addAll(selectedList);
                        performSearch();
                        break;
                    default:
                        Logger.e(TAG, "response:" + response.code() + "-" + response.message());
                        onApproveFailed(response.code() + ":" + response.message());
                        break;
                }
            }

            @Override
            public void onFailure(Call<JSONArray> call, Throwable t) {
                onApproveFailed(t.getMessage());
                ErrorHandler.getInstance().setException(ApprovalMultipleActivity.this, t);
            }
        });
    }

    boolean isSelected(Approval approval) {
        for (Approval selected : selectedList) {
            if (selected.getId().endsWith(approval.getId())) {
                return true;
            }
        }
        return false;
    }

    private void onApproveFailed(String msg) {
        Logger.e(TAG, "onApproveFailed:" + msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//        finish();
    }

    private Approve approvalTransfer(Approval input, String reason, ApprovalStatus status) {
        Approve output = input.getApprove();
        output.setReason(reason);
        output.setApproval(status);
        return output;
    }

    private boolean check() {
        if (selectedList.size() == 0) {
            Toast.makeText(this, R.string.error_msg_no_approval_selected, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }
}
