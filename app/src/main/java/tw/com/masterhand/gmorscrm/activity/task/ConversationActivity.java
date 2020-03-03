package tw.com.masterhand.gmorscrm.activity.task;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.Conversation;
import tw.com.masterhand.gmorscrm.model.Message;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.ItemMessage;

public class ConversationActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.scrollView)
    ScrollView svContainer;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.editText_msg)
    EditText etMsg;
    @BindView(R.id.button_send)
    Button btnSend;
    @BindView(R.id.itemMessage)
    ItemMessage itemMessage;

    Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_conversation));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conversation.getMain() != null) {
                    DatabaseHelper.getInstance(ConversationActivity.this).saveConversation
                            (conversation).observeOn(AndroidSchedulers
                            .mainThread()).subscribe(new SingleObserver<String>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NonNull String s) {
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            ErrorHandler.getInstance().setException(ConversationActivity.this, e);
                        }
                    });
                } else {
                    Logger.e(TAG, "conversation no main message");
                }
            }
        });
        final String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        final String taskId = getIntent().getStringExtra(MyApplication.INTENT_KEY_ID);
        if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(tripId))
            finish();
        String conversationId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CONVERSATION);
        if (TextUtils.isEmpty(conversationId)) {
            conversation = new Conversation();
            conversation.getConversation().setTrip_id(tripId);
            conversation.getConversation().setTask_id(taskId);
            updateConversation(taskId, tripId);
        } else {
            mDisposable.add(DatabaseHelper.getInstance(this).getConversationById(conversationId)
                    .observeOn
                    (AndroidSchedulers.mainThread()).subscribe(new Consumer<Conversation>() {

                @Override
                public void accept(Conversation result) throws Exception {
                    conversation = result;
                    updateConversation(taskId, tripId);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(ConversationActivity.this, throwable);
                    finish();
                }
            }));
        }

    }

    private void updateConversation(String taskId, String tripId) {
        conversation.getConversation().setTask_id(taskId);
        conversation.getConversation().setTrip_id(tripId);
        if (conversation.getMain() != null) {
            itemMessage.setMessage(conversation.getMain());
        } else {
            itemMessage.setVisibility(View.INVISIBLE);
        }
        itemMessage.showIndex(false);
        if (conversation.getMessages().size() > 0) {
            for (Message message : conversation.getMessages()) {
                ItemMessage item = new ItemMessage(this);
                item.showIndex(true);
                item.setMessage(message);
                container.addView(item);
            }
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        svContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                svContainer.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);
    }

    @OnClick(R.id.button_send)
    void send() {
        String input = etMsg.getText().toString();
        if (TextUtils.isEmpty(input))
            return;
        if (conversation.getMain() == null) {
            conversation.getConversation().setMessage(input);
            conversation.getConversation().setCreated_at(new Date());
            conversation.getConversation().setUpdated_at(conversation.getConversation()
                    .getCreated_at());
            conversation.getConversation().setUser_id(TokenManager.getInstance().getUser().getId());
            conversation.setUser(TokenManager.getInstance().getUser());
            itemMessage.setVisibility(View.VISIBLE);
            itemMessage.setMessage(conversation.getMain());
        } else {
            Message message = new Message();
            message.getMessage().setMessage(input);
            message.getMessage().setCreated_at(new Date());
            message.getMessage().setUpdated_at(message.getMessage().getCreated_at());
            message.getMessage().setUser_id(TokenManager.getInstance().getUser().getId());
            message.setUser(TokenManager.getInstance().getUser());
            conversation.getMessages().add(message);
            ItemMessage item = new ItemMessage(this);
            item.showIndex(true);
            item.setMessage(message);
            container.addView(item);
            scrollToBottom();
        }
        etMsg.setText("");
    }

}
