package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class ParticipantSelectCard extends RelativeLayout {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvJob)
    TextView tvJob;

    Context context;
    User participant;

    OnSelectListener listener;

    public void setSelectListner(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onSelect(User participant);
        void onUnselect(User participant);
    }

    public ParticipantSelectCard(Context context) {
        super(context);
        init(context);
    }

    public ParticipantSelectCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ParticipantSelectCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.card_people_select, this);
        ButterKnife.bind(this, view);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (listener != null)
                {
                    if (v.isSelected())
                        listener.onSelect(participant);
                    else
                        listener.onUnselect(participant);
                }
            }
        });
    }

    public User getParticipant() {
        return participant;
    }

    /**
     * 設定參與人資料
     */
    public void setParticipant(User Participant) {
        this.participant = Participant;
        if (!TextUtils.isEmpty(participant.getShowName())) {
            tvName.setText(participant.getShowName());
        }
        if (!TextUtils.isEmpty(participant.getTitle())) {
            tvJob.setText(participant.getTitle());
        }
    }

    public void disableSelect() {
        setOnClickListener(null);
    }
}
