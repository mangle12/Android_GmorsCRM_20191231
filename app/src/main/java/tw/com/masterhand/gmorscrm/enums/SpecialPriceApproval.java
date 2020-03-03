package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 特價-審批類型
public enum SpecialPriceApproval {
    LEVEL_1(1, R.string.special_price_approval_1),
    LEVEL_2(2, R.string.special_price_approval_2),
    LEVEL_3(3, R.string.special_price_approval_3);

    private int code, title;

    SpecialPriceApproval(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static SpecialPriceApproval getTypeByCode(int code) {
        for (SpecialPriceApproval type : SpecialPriceApproval.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
