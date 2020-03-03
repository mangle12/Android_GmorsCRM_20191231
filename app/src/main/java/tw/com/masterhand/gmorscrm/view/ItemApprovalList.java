package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.ApprovalList;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemApprovalList extends LinearLayout {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvReason)
    TextView tvReason;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.containerReason)
    LinearLayout containerReason;
    /*2018/2/9新增*/
    @BindView(R.id.containerEdit)
    RelativeLayout containerEdit;
    @BindView(R.id.tvDefault)
    TextView tvDefault;
    @BindView(R.id.btnEdit)
    Button btnEdit;


    Context context;
    String stageId;
    ApprovalList.ApprovalUser user;
    OnSelectedListener listener;

    public interface OnSelectedListener {
        void onSelected(ItemApprovalList item);
    }

    public void setListener(OnSelectedListener onSelectedListener) {
        listener = onSelectedListener;
    }

    public ItemApprovalList(Context context) {
        super(context);
        init(context);
    }

    public ItemApprovalList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemApprovalList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_approval_list, this);
        ButterKnife.bind(this, view);
    }

    public void setApproval(ApprovalList approvalList, ApprovalList.ApprovalUser user) {
        this.stageId = approvalList.getApproval_stage_id();
        this.user = user;
        if (user.getUser() != null) {
            tvName.setText(user.getUser().getShowName());
            tvTitle.setText(user.getUser().getTitle());
        }
        if (approvalList.getTrip().getSubmit() == SubmitStatus.NONE) {
            containerEdit.setVisibility(VISIBLE);
            if (user.getDefault_user() != null) {
                tvDefault.setText(context.getString(R.string.default_approver) + ":" + user.getDefault_user().getShowName());
            }
        } else {
            containerReason.setVisibility(VISIBLE);
            String statusTitle = context.getString(approvalList.getApproval().getTitle());
            switch (approvalList.getApproval()) {
                case UNSIGN:
                    ivIcon.setImageResource(R.mipmap.sign_approve);
                    tvReason.setText(statusTitle);
                    tvTime.setVisibility(GONE);
                    break;
                case AGREE:
                case DISAGREE:
                    ivIcon.setImageResource(R.mipmap.sign_approved);
                    if (!TextUtils.isEmpty(approvalList.getReason()))
                        tvReason.setText(statusTitle + ":" + approvalList.getReason());
                    else
                        tvReason.setText(statusTitle + ":" + context.getString(R.string
                                .empty_show));
                    tvTime.setText(TimeFormater.getInstance().toDateTimeFormat(approvalList.getUpdated_at()));
                    break;
            }
        }
    }

    public String getStageId() {
        return stageId;
    }

    public ApprovalList.ApprovalUser getStageUser() {
        return user;
    }

    @OnClick(R.id.btnEdit)
    void edit() {
        if (listener != null)
            listener.onSelected(this);
    }
}
