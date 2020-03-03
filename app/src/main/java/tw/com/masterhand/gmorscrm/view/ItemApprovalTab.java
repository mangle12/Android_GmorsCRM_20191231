package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemApprovalTab extends RelativeLayout implements View.OnClickListener {
    @BindView(R.id.tvStatus)
    public TextView tvStatus;
    @BindView(R.id.tvType)
    public TextView tvType;
    @BindView(R.id.tvTitle)
    public TextView tvTitle;
    @BindView(R.id.tvStartTime)
    public TextView tvStartTime;
    @BindView(R.id.tvEndTime)
    public TextView tvEndTime;
    @BindView(R.id.tvContent1)
    public TextView tvContent1;
    @BindView(R.id.tvContent2)
    public TextView tvContent2;
    @BindView(R.id.tvName)
    public TextView tvName;
    @BindView(R.id.tvUpdateTime)
    public TextView tvUpdateTime;

    public Context context;
    public Approval approval;
    OnApprovalSelectListener listener;

    public ItemApprovalTab(Context context) {
        super(context);
        init(context);
    }

    public ItemApprovalTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemApprovalTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    void init(Context mContext) {
        context = mContext;
        if (isInEditMode())
            return;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_approval_tab, this);
        ButterKnife.bind(this, view);
    }

    public void setApproval(final Approval approval) {
        this.approval = approval;
        setStatus(approval.getApproval(), approval);
        tvTitle.setText(approval.getName());
        setName(approval.getUser_id());
        setType(approval.getType());
        if (approval.getDate_type()) {
            tvStartTime.setText(context.getString(R.string.start_time) + ":" + TimeFormater.getInstance().toDateFormat(approval.getFrom_date()));
            tvEndTime.setText(context.getString(R.string.end_time) + ":" + TimeFormater.getInstance().toDateFormat(approval.getTo_date()));
        } else {
            tvStartTime.setText(context.getString(R.string.start_time) + ":" + TimeFormater.getInstance().toDateTimeFormat(approval.getFrom_date()));
            tvEndTime.setText(context.getString(R.string.end_time) + ":" + TimeFormater.getInstance().toDateTimeFormat(approval.getTo_date()));
        }
        tvUpdateTime.setText(TimeFormater.getInstance().toDateFormat(approval.getCreated_at()));
        tvContent1.setVisibility(GONE);
        tvContent2.setVisibility(GONE);
        Logger.e(getClass().getSimpleName(), "approval:" + new Gson().toJson(approval));
        setOnClickListener(this);
    }

    private void setName(final String userId) {
        DatabaseHelper.getInstance(context).getUserById(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                tvName.setText(user.getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(context, throwable);
            }
        });
    }

    private void setType(TripType type) {
        tvType.setText(type.getTitle());
        tvType.setCompoundDrawablesRelativeWithIntrinsicBounds(type.getIcon(), 0, 0, 0);
    }

    protected void setStatus(ApprovalStatus status, Approval approval) {
        int signed = approval.approval_stage;
        int total = approval.approval_required_stage;
        String title = context.getString(status.getTitle());
        switch (status) {
            case PROCESS:
            case UNSIGN:
                tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.sign_approve, 0, 0, 0);
                if (approval.getApproval_resend())
                    title = context.getString(R.string.approval_resend);
                break;
            case AGREE:
                tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.sign_approved, 0, 0, 0);
                break;
            case DISAGREE:
                tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_pending, 0, 0, 0);
                break;
        }
        if (total > 0) {
            tvStatus.setText(title + "(" + signed + "/" + total + ")");
        } else {
            tvStatus.setText(title);
        }
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onApprovalSelect(approval);
    }

    public void setListener(OnApprovalSelectListener mListener) {
        listener = mListener;
    }

    public interface OnApprovalSelectListener {
        void onApprovalSelect(Approval approval);
    }
}
