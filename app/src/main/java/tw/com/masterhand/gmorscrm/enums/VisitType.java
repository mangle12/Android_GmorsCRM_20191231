package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum VisitType {
    // 拜訪類型
    NORMAL(1, R.string.visit_normal),
    PHONE(2, R.string.visit_phone),
    EMAIL(3, R.string.visit_email),
    FAST(4, R.string.visit_fast);

    int value, title;

    VisitType(int value, int title) {
        this.value = value;
        this.title = title;
    }

    public int getTitle() {
        return title;
    }

    public int getValue() {
        return value;
    }

    public static VisitType getTypeByValue(int value) {
        for (VisitType type : VisitType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return NORMAL;
    }
}
