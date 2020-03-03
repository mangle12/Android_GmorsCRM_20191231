package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.task.ConversationActivity;
import tw.com.masterhand.gmorscrm.model.Conversation;
import tw.com.masterhand.gmorscrm.model.Message;

public class ItemConversation extends RelativeLayout {

    @BindView(R.id.itemMessage)
    ItemMessage itemMessage;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.button_reply)
    Button btnReply;

    Context context;
    Conversation conversation;

    public ItemConversation(Context context) {
        super(context);
        init(context);
    }

    public ItemConversation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemConversation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_conversation, this);
        ButterKnife.bind(this, view);
    }

    public void setConversation(Conversation c) {
        conversation = c;
        itemMessage.setMessage(conversation.getMain());
        for (Message msg : conversation.getMessages()) {
            addMessage(generateItemMessage(msg));
        }
    }

    public ItemMessage getMainMessage() {
        return itemMessage;
    }

    public void addMessage(ItemMessage message) {
        container.addView(message);
    }

    private ItemMessage generateItemMessage(Message message) {
        ItemMessage item = new ItemMessage(context);
        item.setMessage(message);
        return item;
    }

    @OnClick(R.id.button_reply)
    void reply() {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, conversation.getConversation().getTrip_id());
        intent.putExtra(MyApplication.INTENT_KEY_ID, conversation.getConversation().getTask_id());
        intent.putExtra(MyApplication.INTENT_KEY_CONVERSATION, conversation.getConversation().getId());
        ((Activity) context).startActivityForResult(intent, MyApplication.REQUEST_ADD_CONVERSATION);
    }
}
