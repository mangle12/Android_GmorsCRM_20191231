package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 簽到狀態
public enum SignStatus {
    NONE(0, R.string.sign_none),
    SIGN_IN(1, R.string.sign_in_done),
    SIGN_OUT(2, R.string.sign_out_done);

    private int code, title;

    SignStatus(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static SignStatus getStatusByCode(int code) {
        for (SignStatus status : SignStatus.values()) {
            if (code == status.getCode())
                return status;
        }
        return null;
    }
}
