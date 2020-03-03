package tw.com.masterhand.gmorscrm.activity.approval;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.model.Approve;
import tw.com.masterhand.gmorscrm.tools.GsonGMTDateAdapter;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemApprovalTab;

public class ApprovalTabActivity extends BaseWebCheckActivity implements ItemApprovalTab
        .OnApprovalSelectListener {
    final int REQUEST_DETAIL = 200, REQUEST_MULTI = 201;

    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.btnUnsign)
    Button btnUnsign;
    @BindView(R.id.btnAgree)
    Button btnAgree;
    @BindView(R.id.btnDisagree)
    Button btnDisagree;

    boolean isRecordMode;
    TripType tripType;
    List<Approval> approvalList;

    ApprovalStatus currentStatus;
    Approval selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_tab);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        btnUnsign.setSelected(true);
        updateList(ApprovalStatus.UNSIGN);
    }

    private void init() {
        gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonGMTDateAdapter()).create();
        try {
            isRecordMode = getIntent().getBooleanExtra(MyApplication.INTENT_KEY_MODE, false);
            Logger.e(TAG, "isRecordMode:" + isRecordMode);

            tripType = TripType.getTripTypeByValue(getIntent().getIntExtra(MyApplication.INTENT_KEY_TYPE, 1));
            Logger.e(TAG, "getIntent:" + getIntent().getStringExtra(MyApplication.INTENT_KEY_APPROVAL));

            approvalList = gson.fromJson(getIntent().getStringExtra(MyApplication.INTENT_KEY_APPROVAL), new TypeToken<List<Approval>>() {}.getType());
            Collections.sort(approvalList, new Comparator<Approval>() {
                @Override
                public int compare(Approval o1, Approval o2) {
                    return o2.getCreated_at().compareTo(o1.getCreated_at());
                }
            });

            if (isRecordMode)
                appbar.setTitle(getString(R.string.main_menu_approval_record) + "/" + getString(tripType.getTitle()));
            else {

                appbar.setTitle(getString(R.string.main_menu_approval_sign) + "/" + getString(tripType.getTitle()));
                appbar.addFunction(R.mipmap.common_plural, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 多數審批
                        List<Approval> unSignList = new ArrayList<>();
                        for (Approval approval : approvalList) {
                            if (approval.getApproval() == ApprovalStatus.UNSIGN || approval.getApproval() == ApprovalStatus.PROCESS)
                            {
                                boolean isSign = false;
                                String userId = TokenManager.getInstance().getUser().getId();
                                for (Approve approve : approval.getTrip_approvals()) {
                                    if (approve.getUser_id().equals(userId)) {
                                        isSign = true;
                                        break;
                                    }
                                }
                                if (!isSign) {
                                    unSignList.add(approval);
                                }
                            }
                        }

                        Intent intent = new Intent(ApprovalTabActivity.this, ApprovalMultipleActivity.class);
                        intent.putExtra(MyApplication.INTENT_KEY_APPROVAL, gson.toJson(unSignList));
                        startActivityForResult(intent, REQUEST_MULTI);
                    }
                });
            }
            appbar.addFunction(R.mipmap.common_search, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 執行搜尋
                    Intent intent = new Intent(ApprovalTabActivity.this, ApprovalSearchActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_APPROVAL, gson.toJson(approvalList));
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            Logger.e(TAG, "Exception:" + e.getMessage());
        }
    }

    private void updateList(ApprovalStatus status) {
        if (isRecordMode)
            updateRecordList(status);
        else
            updateApprovalList(status);
    }

    /**
     * 更新審批執行列表
     */
    private void updateApprovalList(ApprovalStatus status) {
        currentStatus = status;
        container.removeAllViews();
        switch (status) {
            case UNSIGN:
                for (Approval approval : approvalList) {
                    if (approval.getApproval() == ApprovalStatus.UNSIGN || approval.getApproval() == ApprovalStatus.PROCESS) {
                        boolean isSign = false;
                        String userId = TokenManager.getInstance().getUser().getId();
                        for (Approve approve : approval.getTrip_approvals()) {
                            if (approve.getUser_id().equals(userId)) {
                                isSign = true;
                                break;
                            }
                        }
                        if (!isSign) {
                            addItem(approval);
                        }
                    }
                }
                break;
            default:
                for (Approval approval : approvalList) {
                    if (approval.getApproval() == status)
                    {
                        addItem(approval);
                    }
                    else if (approval.getApproval() == ApprovalStatus.PROCESS)
                    {
                        for (Approve approve : approval.getTrip_approvals()) {
                            if (approve.getUser_id().equals(TokenManager.getInstance().getUser().getId())) {
                                if (approve.getApproval() == status) {
                                    addItem(approval);
                                }
                                break;
                            }
                        }
                    }
                }
        }
        if (container.getChildCount() == 0)
            container.addView(getEmptyImageView(null));
    }

    /**
     * 更新申請明細列表
     */
    private void updateRecordList(ApprovalStatus status) {
        currentStatus = status;
        container.removeAllViews();
        if (status == ApprovalStatus.UNSIGN) {
            for (Approval approval : approvalList) {
                if (approval.getApproval() == ApprovalStatus.UNSIGN || approval.getApproval() == ApprovalStatus.PROCESS) {
                    addItem(approval);
                }
            }
        } else {
            for (Approval approval : approvalList) {
                if (approval.getApproval() == status) {
                    addItem(approval);
                }
            }
        }
        if (container.getChildCount() == 0)
            container.addView(getEmptyImageView(null));
    }

    private void addItem(Approval approval) {
        ItemApprovalTab item = new ItemApprovalTab(this);
        item.setApproval(approval);
        item.setListener(this);
        container.addView(item);
    }

    @OnClick(R.id.btnUnsign)
    void selectUnsign(View v) {
        clearSelect();
        v.setSelected(true);
        updateList(ApprovalStatus.UNSIGN);
    }

    @OnClick(R.id.btnAgree)
    void selectAgree(View v) {
        clearSelect();
        v.setSelected(true);
        updateList(ApprovalStatus.AGREE);
    }

    @OnClick(R.id.btnDisagree)
    void selectDisagree(View v) {
        clearSelect();
        v.setSelected(true);
        updateList(ApprovalStatus.DISAGREE);
    }

    private void clearSelect() {
        btnUnsign.setSelected(false);
        btnAgree.setSelected(false);
        btnDisagree.setSelected(false);
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
            case SPECIAL_PRICE:
                intent = new Intent(this, ApprovalSpecialPriceActivity.class);
                break;
            case PRODUCTION:
                intent = new Intent(this, ApprovalProductionActivity.class);
                break;
            case NON_STANDARD_INQUIRY:
                intent = new Intent(this, ApprovalNonStandardInquiryActivity.class);
                break;
            case SPRING_RING_INQUIRY:
                intent = new Intent(this, ApprovalSpringRingInquiryActivity.class);
                break;
            case EXPRESS:
                intent = new Intent(this, ApprovalExpressActivity.class);
                break;
            case TRAVEL:
                intent = new Intent(this, ApprovalTravelActivity.class);
                break;
            case SPECIAL_SHIP:
                intent = new Intent(this, ApprovalSpecialShipActivity.class);
                break;
        }

        if (intent != null) {
            Logger.e(TAG, "trip id:" + approval.getId());
            intent.putExtra(MyApplication.INTENT_KEY_TRIP, approval.getId());
            intent.putExtra(MyApplication.INTENT_KEY_APPROVAL, gson.toJson(approval));
            intent.putExtra(MyApplication.INTENT_KEY_ENABLE, isRecordMode);
            startActivityForResult(intent, REQUEST_DETAIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DETAIL:
                if (resultCode == ApprovalStatus.AGREE.getCode()) {
                    boolean isAllAgree = false;
                    int stage = selected.getTrip_approvals().size();
                    for (Approve approve : selected.getTrip_approvals()) {
                        if (TextUtils.isEmpty(approve.getUser_id())) {
                            approve.setUser_id(TokenManager.getInstance().getUser().getId());
                            approve.setApproval(ApprovalStatus.AGREE);
                            break;
                        }
                    }
                    for (Approve approve : selected.getTrip_approvals()) {
                        if (approve.getStage() == stage && approve.getApproval() == ApprovalStatus.AGREE)
                            isAllAgree = true;
                    }
                    if (isAllAgree)
                    {
                        selected.approval_stage++;
                        selected.setApproval(ApprovalStatus.AGREE);
                    }
                    else
                    {
                        selected.setApproval(ApprovalStatus.PROCESS);
                    }
                    Logger.e(TAG, "selected changed:" + gson.toJson(selected));

                } else if (resultCode == ApprovalStatus.DISAGREE.getCode()) {
                    for (Approve approve : selected.getTrip_approvals()) {
                        if (TextUtils.isEmpty(approve.getUser_id())) {
                            approve.setUser_id(TokenManager.getInstance().getUser().getId());
                            approve.setApproval(ApprovalStatus.DISAGREE);
                            break;
                        }
                    }
                    selected.setApproval(ApprovalStatus.DISAGREE);
                } else if (resultCode == ApprovalStatus.UNSIGN.getCode()) {
                    selected.approval_stage = 0;
                    selected.setApproval(ApprovalStatus.UNSIGN);
                }
                selected = null;
                updateList(currentStatus);
                break;
            case REQUEST_MULTI:
                // 多數審批後刷新列表
                if (resultCode == RESULT_OK) {
                    for (Iterator<Approval> iter = approvalList.listIterator(); iter.hasNext(); )
                    {
                        Approval approval = iter.next();
                        if (approval.getApproval() == ApprovalStatus.UNSIGN || approval.getApproval() == ApprovalStatus.PROCESS) {
                            boolean isSign = false;
                            String userId = TokenManager.getInstance().getUser().getId();
                            for (Approve approve : approval.getTrip_approvals()) {
                                if (approve.getUser_id().equals(userId)) {
                                    isSign = true;
                                    break;
                                }
                            }
                            if (!isSign) {
                                iter.remove();
                            }
                        }
                    }

                    String approvalString = data.getStringExtra(MyApplication.INTENT_KEY_APPROVAL);
                    List<Approval> change = gson.fromJson(approvalString, new TypeToken<List<Approval>>() {}.getType());
                    approvalList.addAll(change);
                    updateList(currentStatus);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
