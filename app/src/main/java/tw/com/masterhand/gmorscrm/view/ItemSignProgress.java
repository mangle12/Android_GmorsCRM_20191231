package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;

public class ItemSignProgress extends LinearLayout {
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_status)
    TextView tvStatus;
    @BindView(R.id.textView_time)
    TextView tvTime;

    Context context;
//    Signer signer;

    public ItemSignProgress(Context context) {
        super(context);
        init(context);
    }

    public ItemSignProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSignProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        if (isInEditMode())
            return;
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_sign_progress, this);
        ButterKnife.bind(this, view);
    }

//    public void setSigner(Signer mSigner, String name) {
//        signer = mSigner;
//        tvName.setText(name);
//        tvStatus.setText(context.getString(signer.getStatus().getTitle()));
//        tvStatus.setTextColor(ContextCompat.getColor(context, signer.getStatus().getColor()));
//        try {
//            tvTime.setText(TimeFormater.toDateTimeFormat(signer.getUpdated_at()));
//        } catch (ParseException e) {
//            tvTime.setText(R.string.empty_show);
//        }
//    }

}
