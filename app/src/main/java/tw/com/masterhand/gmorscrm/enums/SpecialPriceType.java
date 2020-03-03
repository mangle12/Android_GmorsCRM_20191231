package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 特價類型
public enum SpecialPriceType {
    SINGLE(0, R.string.special_price_type_single),
    FREQUENT(1, R.string.special_price_type_frequent);

    private int code, title;

    SpecialPriceType(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static SpecialPriceType getTypeByCode(int code) {
        for (SpecialPriceType type : SpecialPriceType.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
