package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.model.ParticipantWithUser;

public class ItemParticipantList extends LinearLayout {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvSignIn)
    TextView tvSignIn;
    @BindView(R.id.tvSignOut)
    TextView tvSignOut;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvStatus)
    TextView tvStatus;

    Context context;
    ParticipantWithUser participant;

    public ItemParticipantList(Context context) {
        super(context);
        init(context);
    }

    public ItemParticipantList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemParticipantList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_participant_list, this);
        ButterKnife.bind(this, view);
    }

    public void setParticipant(ParticipantWithUser participantWithUser) {
        participant = participantWithUser;
        tvName.setText(participant.getUser().getShowName());
        tvTitle.setText(participantWithUser.getUser().getTitle());
        tvStatus.setText(participant.getParticipant().getAccept().getTitle());
        if (participant.getParticipant().getSign_in_at() != null) {
            tvSignIn.setTextColor(ContextCompat.getColor(context, R.color.gray));
            tvSignIn.setText(TimeFormater.getInstance().toDateTimeFormat(participant.getParticipant().getSign_in_at()) + context.getString(R.string.sign_in));
        }
        if (participant.getParticipant().getSign_out_at() != null) {
            tvSignOut.setTextColor(ContextCompat.getColor(context, R.color.gray));
            tvSignOut.setText(TimeFormater.getInstance().toDateTimeFormat(participant.getParticipant().getSign_out_at()) + context.getString(R.string.sign_out));
        }
    }
}
