package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 特價-項目狀態
public enum SpecialPriceProjectStatus {
    SAMPLE(0, R.string.special_price_project_status_sample),
    SMALL(1, R.string.special_price_project_status_small),
    BIG(2, R.string.special_price_project_status_big);

    private int code, title;

    SpecialPriceProjectStatus(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static SpecialPriceProjectStatus getTypeByCode(int code) {
        for (SpecialPriceProjectStatus type : SpecialPriceProjectStatus.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
