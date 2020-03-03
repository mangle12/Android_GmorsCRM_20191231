package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 快遞類型
public enum ExpressType {
    FREE(0, R.string.express_type_free),
    VALUE(1, R.string.express_type_value);

    private int code, title;

    ExpressType(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static ExpressType getTypeByCode(int code) {
        for (ExpressType type : ExpressType.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
