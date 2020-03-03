package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum ReimbursementTypeLimit {
    // 報銷單扣除對象限定
    NONE(0),
    SELF(1),
    DEPARTMENT(2),
    NOT_REQUIRED(3);

    int value, title;

    ReimbursementTypeLimit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ReimbursementTypeLimit getTypeByValue(int value) {
        for (ReimbursementTypeLimit type : ReimbursementTypeLimit.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return SELF;
    }
}
