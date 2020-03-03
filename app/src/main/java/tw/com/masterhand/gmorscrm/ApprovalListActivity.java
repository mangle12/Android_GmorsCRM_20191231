package tw.com.masterhand.gmorscrm;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.model.ApprovalList;
import tw.com.masterhand.gmorscrm.model.ModifyApprovers;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemApprovalList;

public class ApprovalListActivity extends BaseWebCheckActivity implements ItemApprovalList
        .OnSelectedListener {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    String tripId;
    ItemApprovalList selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_list);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateList();
    }

    private void init() {
        appbar.setTitle(getString(R.string.approver));
        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
    }

    private ItemApprovalList generateItem(ApprovalList list, ApprovalList.ApprovalUser user) {
        ItemApprovalList item = new ItemApprovalList(this);
        item.setApproval(list, user);
        item.setListener(this);
        return item;
    }


    private void updateList() {
        container.removeAllViews();
        Call<JSONObject> call = ApiHelper.getInstance().getApprovalApi().getApprovableStage
                (TokenManager.getInstance()
                        .getToken(), tripId);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            String listString = result.getString("list");
                            Logger.e(TAG, "approval list:" + listString);
                            List<ApprovalList> approvalList = gson.fromJson(listString, new
                                    TypeToken<List<ApprovalList>>() {
                                    }.getType());
                            Collections.sort(approvalList, new Comparator<ApprovalList>() {
                                public int compare(ApprovalList list1, ApprovalList list2) {
                                    return list1.getStage() > list2.getStage() ? 1 : -1;
                                }
                            });
                            if (approvalList.size() > 0) {
                                int currentStage = 0;
                                for (ApprovalList list : approvalList) {
                                    // 關卡Title
                                    if (list.getStage() > currentStage) {
                                        currentStage = list.getStage();
                                        TextView tvTitle = new TextView(ApprovalListActivity.this);
                                        tvTitle.setText("" + list.getStage() + getString(R.string
                                                .approval_stage));
                                        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                        tvTitle.setPaddingRelative(UnitChanger.dpToPx(16),
                                                UnitChanger.dpToPx(8), 0, UnitChanger.dpToPx(8));
                                        tvTitle.setBackgroundResource(R.drawable.bg_input_required);
                                        if (container.getChildCount() == 0) {
                                            container.addView(tvTitle);
                                        } else {
                                            LinearLayout.LayoutParams params = new LinearLayout
                                                    .LayoutParams(LinearLayout.LayoutParams
                                                    .MATCH_PARENT, LinearLayout.LayoutParams
                                                    .WRAP_CONTENT);
                                            params.setMargins(0, UnitChanger.dpToPx(30), 0, 0);
                                            container.addView(tvTitle, params);
                                        }
                                    }
                                    // 審批人
                                    if (list.getApproval() == ApprovalStatus.UNSIGN || list
                                            .getApproval() == ApprovalStatus.PROCESS) {
                                        // 未審批顯示全部待審批人員
                                        if (list.getApproval_stage() != null)
                                            for (ApprovalList.ApprovalUser approvalUser : list
                                                    .getApproval_stage().getUsers()) {
                                                container.addView(generateItem(list, approvalUser));
                                            }
                                    } else {
                                        // 已審批只顯示審批者
                                        ApprovalList.ApprovalUser user = null;
                                        if (list.getApproval_stage() != null)
                                            for (ApprovalList.ApprovalUser approvalUser : list
                                                    .getApproval_stage().getUsers()) {
                                                if (list.getUser_id().equals(approvalUser.getUser()
                                                        .getId())) {
                                                    user = approvalUser;
                                                    break;
                                                }
                                            }
                                        if (user != null)
                                            container.addView(generateItem(list, user));
                                    }
                                }
                            } else {
                                container.addView(getEmptyImageView(getString(R.string
                                        .error_msg_no_approval)));
                            }
                            break;
                        default:
                            Toast.makeText(ApprovalListActivity.this, response.code() + ":" +
                                    response.message(), Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    ErrorHandler.getInstance().setException(ApprovalListActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(ApprovalListActivity.this, t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_SELECT_USER:
                    String userId = data.getStringExtra(MyApplication.INTENT_KEY_USER);
                    if (!TextUtils.isEmpty(userId))
                        changeApprover(selectedItem.getStageId(), selectedItem.getStageUser()
                                .getId(), userId);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void changeApprover(String stageId, String stageUserId, String userId) {
        startProgressDialog();
        ModifyApprovers modifyApprovers = new ModifyApprovers();
        modifyApprovers.setTrip_id(tripId);
        modifyApprovers.setApproval_stage_id(stageId);
        modifyApprovers.setApproval_stage_user_id(stageUserId);
        modifyApprovers.setUser_id(userId);
        ApiHelper.getInstance().getApprovalApi().modifyApprovers(TokenManager.getInstance()
                .getToken(), modifyApprovers).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                stopProgressDialog();
                try {
                    switch (response.code()) {
                        case 200:
                            updateList();
                            break;
                        default:
                            Logger.e(TAG, "modify approver failed:" + response.code());
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException
                            (ApprovalListActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                stopProgressDialog();
                ErrorHandler.getInstance().setException
                        (ApprovalListActivity.this, t);
            }
        });
    }

    @Override
    public void onSelected(ItemApprovalList item) {
        selectedItem = item;
        Intent intent = new Intent(this, UserSelectActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_APPROVER, selectedItem.getStageUser().getUser_id
                ());
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_USER);
    }
}
