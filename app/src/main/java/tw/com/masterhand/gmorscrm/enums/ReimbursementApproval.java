package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 報銷-審批類型
public enum ReimbursementApproval {
    LEVEL_1(1, R.string.reimbursement_approval_1),
    LEVEL_2(2, R.string.reimbursement_approval_2);

    private int code, title;

    ReimbursementApproval(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static ReimbursementApproval getTypeByCode(int code) {
        for (ReimbursementApproval type : ReimbursementApproval.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
