package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum ReimbursementType {
    // 報銷單扣除對象
    NONE(0, R.string.no_select),
    SELF(1, R.string.reimbursement_type_self),
    DEPARTMENT(2, R.string.reimbursement_type_department);

    int value, title;

    ReimbursementType(int value, int title) {
        this.value = value;
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public int getTitle() {
        return title;
    }

    public static ReimbursementType getReimbursementTypeByValue(int value) {
        for (ReimbursementType type : ReimbursementType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return SELF;
    }
}
