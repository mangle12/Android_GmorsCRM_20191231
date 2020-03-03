package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.task.ReimbursementDetailActivity;
import tw.com.masterhand.gmorscrm.model.ReimbursementWithConfig;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ExpRecordCard extends RelativeLayout {
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_time)
    TextView tvTime;
    @BindView(R.id.textView_id)
    TextView tvCode;

    Context context;
    ReimbursementWithConfig reimbursement;

    public ExpRecordCard(Context context) {
        super(context);
        init(context);
    }

    public ExpRecordCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpRecordCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        if (isInEditMode())
            return;

        context = mContext;

        // 連接畫面
        View view = inflate(getContext(), R.layout.card_exp_record, this);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reimbursement == null)
                    return;

                Intent intent = new Intent(context, ReimbursementDetailActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_TRIP, reimbursement.getReimbursement().getTrip_id());
                context.startActivity(intent);
            }
        });
    }

    public void setReocrd(ReimbursementWithConfig reimbursement) {
        this.reimbursement = reimbursement;
        tvTitle.setText(reimbursement.getTrip().getName());
        tvTime.setText(TimeFormater.getInstance().toDateFormat(reimbursement.getTrip().getCreated_at()));
        if (!TextUtils.isEmpty(reimbursement.getReimbursement().getCode())) {
            tvCode.setText("ID:" + reimbursement.getTrip().getCode());
        } else {
            tvCode.setText("ID:" + context.getString(R.string.error_msg_code_not_found));
        }
    }
}
