package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 審批狀態
public enum ApprovalRequire {
    UNKNOWN(0, R.string.approval_require_unknown),
    NEED(1, R.string.approval_require_need),
    NOT_NEED(2, R.string.approval_require_not_need);

    private int code, title;

    ApprovalRequire(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static ApprovalRequire getItemByCode(int code) {
        for (ApprovalRequire item : ApprovalRequire.values()) {
            if (item.getCode() == code)
                return item;
        }
        return UNKNOWN;
    }
}
