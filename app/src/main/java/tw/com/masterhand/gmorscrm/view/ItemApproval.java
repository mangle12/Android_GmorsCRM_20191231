package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.model.Approve;

public class ItemApproval extends LinearLayout {
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.btnResend)
    RelativeLayout btnResend;
    @BindView(R.id.btnAgree)
    RelativeLayout btnAgree;
    @BindView(R.id.btnDisagree)
    RelativeLayout btnDisagree;

    Context context;
    OnApprovalSelectListener listener;
    Approval approval;

    public interface OnApprovalSelectListener {
        void onApprovalSelect(String tripId, ApprovalStatus status);
        void resend();
    }

    public ItemApproval(Context context) {
        super(context);
        init(context);
    }

    public ItemApproval(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemApproval(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_approval, this);
        ButterKnife.bind(this, view);
    }

    public void setStatus(ApprovalStatus status, OnApprovalSelectListener approvalSelectListener) {
        btnAgree.setVisibility(GONE);
        btnDisagree.setVisibility(GONE);
        tvStatus.setVisibility(VISIBLE);
        tvStatus.setText(status.getTitle());
        switch (status) {
            case UNSIGN:
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                btnResend.setVisibility(GONE);
                break;
            case AGREE:
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                btnResend.setVisibility(GONE);
                break;
            case DISAGREE:
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                btnResend.setVisibility(VISIBLE);
                listener = approvalSelectListener;
                btnResend.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null)
                            listener.resend();
                    }
                });
                break;
        }
    }

    public void setApproval(final Approval approval, OnApprovalSelectListener approvalSelectListener) {
        tvStatus.setVisibility(GONE);
        btnResend.setVisibility(GONE);
        this.approval = approval;
        ApprovalStatus approvalStatus = approval.getApproval();
        switch (approvalStatus) {
            case PROCESS:
            case UNSIGN:
                // 從trip_approvals中檢查自己是否已簽核
                boolean isSign = false;
                for (Approve approve : approval.getTrip_approvals()) {
                    if (!TextUtils.isEmpty(approve.getUser_id()) && approve.getUser_id().equals
                            (TokenManager.getInstance().getUser().getId())) {
                        switch (approve.getApproval()) {
                            case AGREE:
                                btnAgree.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                                isSign = true;
                                break;
                            case DISAGREE:
                                btnDisagree.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                                isSign = true;
                                break;
                        }
                        break;
                    }
                }
                if (!isSign) {
                    btnAgree.setVisibility(VISIBLE);
                    btnDisagree.setVisibility(VISIBLE);
                    listener = approvalSelectListener;
                    btnAgree.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listener != null)
                                listener.onApprovalSelect(approval.getId(), ApprovalStatus.AGREE);
                        }
                    });
                    btnDisagree.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listener != null)
                                listener.onApprovalSelect(approval.getId(), ApprovalStatus.DISAGREE);
                        }
                    });
                }
        }
    }

}
