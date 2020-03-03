package tw.com.masterhand.gmorscrm.enums;

public enum NotificationType {
    CONVERSATION(0),
    APPROVAL(1),
    INVITATION(2),
    ANNOUNCE(3),
    APPROVE(4),
    PARTICIPANT(6);

    int value;

    NotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static NotificationType getTypeByValue(int value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
