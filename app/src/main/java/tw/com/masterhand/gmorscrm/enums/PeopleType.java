package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum PeopleType {
    CONTACT(0, R.string.contact),
    WORKER(1, R.string.other_worker),
    JOINER(10, R.string.participant),
    RELATED_WORKER(11, R.string.related_worker),
    REVIEWER(12, R.string.reviewer);

    private int code, title;

    PeopleType(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static PeopleType getPeopleTypeByCode(int code) {
        for (PeopleType type : PeopleType.values()) {
            if (code == type.getCode())
                return type;
        }
        return null;
    }
}
