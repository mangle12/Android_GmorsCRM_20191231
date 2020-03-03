package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.Message;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemMessage extends RelativeLayout {

    @BindView(R.id.textView_time)
    TextView tvTime;
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_content)
    TextView tvContent;
    @BindView(R.id.imageView_icon)
    ImageView ivHead;

    Context context;
    Message message;

    public ItemMessage(Context context) {
        super(context);
        init(context);
    }

    public ItemMessage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemMessage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;

        // 連接畫面
        View view = inflate(getContext(), R.layout.item_message, this);
        ButterKnife.bind(this, view);
    }

    public void showIndex(boolean isShow) {
        if (isShow)
            findViewById(  R.id.relativeLayout_index).setVisibility(VISIBLE);
        else
            findViewById(  R.id.relativeLayout_index).setVisibility(GONE);
    }

    public void setMessage(Message m) {
        if (m == null) {
            Logger.e(getClass().getSimpleName(), "Message is null");
            return;
        }
        message = m;
        if (message.getUser() != null)
            tvName.setText(message.getUser().getShowName());
        if (message.getMessage() != null) {
            tvContent.setText(message.getMessage().getMessage());
            tvTime.setText(TimeFormater.getInstance().toDateTimeFormat(message.getMessage().getCreated_at()));
        }
    }
}
