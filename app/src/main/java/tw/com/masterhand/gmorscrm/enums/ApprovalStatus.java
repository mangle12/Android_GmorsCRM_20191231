package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 審批狀態
public enum ApprovalStatus {
    UNSIGN(0, R.string.approval_status_unsign, R.color.orange),
    AGREE(2, R.string.approval_status_agree, R.color.gray),
    DISAGREE(3, R.string.approval_status_disagree, R.color.gray),
    PROCESS(4, R.string.approval_status_process, R.color.gray);

    private int code, title, color;

    ApprovalStatus(int code, int title, int color) {
        this.code = code;
        this.title = title;
        this.color = color;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }

    public static ApprovalStatus getStatusByCode(int code) {
        for (ApprovalStatus status : ApprovalStatus.values()) {
            if (status.getCode() == code)
                return status;
        }
        return null;
    }
}
