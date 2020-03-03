package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 回款類型
public enum RepaymentType {
    TRANSFER(0, R.string.repayment_type_transfer),
    BILL(1, R.string.repayment_type_bill);

    private int code, title;

    RepaymentType(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static RepaymentType getTypeByCode(int code) {
        for (RepaymentType type : RepaymentType.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
