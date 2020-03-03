package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 參與類型
public enum AcceptType {
    NONE(0, R.string.accept_none, R.color.gray),
    NO(1, R.string.accept_no, R.color.red),
    MAYBE(2, R.string.accept_maybe, R.color.black),
    YES(3, R.string.accept_yes, R.color.blue);

    private int code, title, color;

    AcceptType(int code, int title, int color) {
        this.code = code;
        this.title = title;
        this.color = color;
    }

    public int getCode() {
        return code;
    }

    public int getColor() {
        return color;
    }

    public int getTitle() {
        return title;
    }

    public static AcceptType getTypeByCode(int code) {
        for (AcceptType type : AcceptType.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
