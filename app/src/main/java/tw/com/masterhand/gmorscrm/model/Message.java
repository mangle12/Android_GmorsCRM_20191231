package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.room.record.TaskMessage;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class Message {
    TaskMessage message;
    User user;

    public Message() {
        message = new TaskMessage();
    }

    public TaskMessage getMessage() {
        return message;
    }

    public void setMessage(TaskMessage message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
