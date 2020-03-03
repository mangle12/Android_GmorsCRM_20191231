package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum ParentType {
    VISIT(1, R.string.work_visit),
    OFFICE(2, R.string.work_office),
    TASK(3, R.string.work_task),
    ABSENT(4, R.string.work_absent),
    QUOTATION(5, R.string.apply_quotation),
    CONTRACT(6, R.string.apply_contract),
    REIMBURSEMENT(7, R.string.apply_reimbursement),
    SAMPLE(8, R.string.apply_sample),
    TRIP(9, R.string.trip),
    PROJECT(10, R.string.project),
    CONTACT_PERSON(11, R.string.contact),
    PARTICIPANT(12, R.string.participant),
    SPECIAL_PRICE(13, R.string.apply_special_price),
    PRODUCTION(14, R.string.apply_production),
    NON_STANDARD_INQUIRY(15, R.string.apply_non_standard_inquiry),
    SPRING_RING_INQUIRY(16, R.string.apply_spring_ring_inquiry),
    SPECIAL_SHIP(17, R.string.apply_special_ship),
    REPAYMENT(18, R.string.apply_repayment),
    EXPRESS(19, R.string.apply_express),
    TRAVEL(20, R.string.apply_travel);

    int value, title;

    ParentType(int value, int title) {
        this.value = value;
        this.title = title;
    }

    public int getTitle() {
        return title;
    }

    public int getValue() {
        return value;
    }

    public static ParentType getTypeByValue(int value) {
        for (ParentType parentType : ParentType.values()) {
            if (parentType.getValue() == value) {
                return parentType;
            }
        }
        return null;
    }
}
