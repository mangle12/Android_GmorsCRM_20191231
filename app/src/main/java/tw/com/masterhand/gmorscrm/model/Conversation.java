package tw.com.masterhand.gmorscrm.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.room.record.TaskConversation;
import tw.com.masterhand.gmorscrm.room.record.TaskMessage;
import tw.com.masterhand.gmorscrm.room.setting.User;


public class Conversation {
    TaskConversation conversation;
    User user;
    List<Message> messages;

    public Conversation() {
        super();
        conversation = new TaskConversation();
        messages = new ArrayList<>();
    }

    public TaskConversation getConversation() {
        return conversation;
    }

    public void setConversation(TaskConversation conversation) {
        this.conversation = conversation;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getMain() {
        if (TextUtils.isEmpty(conversation.getMessage()))
            return null;
        Message msg = new Message();
        msg.setUser(user);
        TaskMessage taskMessage = new TaskMessage();
        taskMessage.setTrip_id(conversation.getTrip_id());
        taskMessage.setMessage(conversation.getMessage());
        taskMessage.setUpdated_at(conversation.getUpdated_at());
        taskMessage.setCreated_at(conversation.getCreated_at());
        msg.setMessage(taskMessage);
        return msg;
    }
}
