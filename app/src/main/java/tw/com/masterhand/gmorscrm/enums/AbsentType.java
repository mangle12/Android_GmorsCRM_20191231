package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

// 假單類型
public enum AbsentType {
    LEAVE(1, R.string.absent_type_leave),
    SICK(2, R.string.absent_type_sick),
    DEAD(3, R.string.absent_type_dead),
    MARRIAGE(4, R.string.absent_type_marriage),
    MATERNITY(5, R.string.absent_type_maternity),
    PUBLIC(6, R.string.absent_type_public),
    TAKING_CARE(7, R.string.absent_type_taking_care),
    PHYSIOLOGICAL(8, R.string.absent_type_physiological),
    BIRTH_CHECK(9, R.string.absent_type_birthCheck),
    PATERNITY(10, R.string.absent_type_paternity),
    PUBLIC_INJURY(11, R.string.absent_type_publicInjury),
    SPECIAL(12, R.string.absent_type_special),
    UNPAID(13, R.string.absent_type_unpaid);

    private int code, title;

    AbsentType(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static AbsentType getTypeByCode(int code) {
        for (AbsentType type : AbsentType.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
