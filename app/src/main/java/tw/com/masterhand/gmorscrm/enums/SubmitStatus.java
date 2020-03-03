package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 提交狀態
public enum SubmitStatus {
    NONE(0, R.string.not_submitted, R.mipmap.submit),
    SUBMITTED(1, R.string.submitted, R.mipmap.submitted);

    private int code, title, icon;

    SubmitStatus(int code, int title, int icon) {
        this.code = code;
        this.title = title;
        this.icon = icon;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public static SubmitStatus getStatusByCode(int code) {
        for (SubmitStatus status : SubmitStatus.values()) {
            if (code == status.getCode())
                return status;
        }
        return NONE;
    }
}
