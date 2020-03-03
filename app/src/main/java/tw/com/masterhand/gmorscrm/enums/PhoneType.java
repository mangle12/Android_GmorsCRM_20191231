package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 電話類型
public enum PhoneType {
    COMPANY(0, R.string.phone_type_company),
    HOME(1, R.string.phone_type_home),
    MOBILE(2, R.string.phone_type_mobile),
    PRIMARY(3, R.string.phone_type_primary),
    COMPANY_FAX(4, R.string.phone_type_company_fax),
    HOME_FAX(5, R.string.phone_type_home_fax),
    OTHER(7, R.string.phone_type_other);

    private int code, title;

    PhoneType(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static PhoneType getPhoneTypeByCode(int code) {
        for (PhoneType type : PhoneType.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
