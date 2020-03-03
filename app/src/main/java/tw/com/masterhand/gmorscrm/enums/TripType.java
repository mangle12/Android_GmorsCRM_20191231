package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum TripType {
    // 工作
    VISIT(1, R.mipmap.common_worktype1, R.string.work_visit, R.color.visit),
    OFFICE(2, R.mipmap.common_worktype2, R.string.work_office, R.color.office),
    TASK(3, R.mipmap.common_worktype3, R.string.work_task, R.color.task),
    ABSENT(4, R.mipmap.common_worktype4, R.string.work_absent, R.color.absent),
    // 申請
    QUOTATION(5, R.mipmap.common_application1, R.string.apply_quotation, R.color.quotation),
    CONTRACT(6, R.mipmap.common_application2, R.string.apply_contract, R.color.contract),
    REIMBURSEMENT(7, R.mipmap.common_application3, R.string.apply_reimbursement, R.color
            .reimbursement),
    SAMPLE(8, R.mipmap.common_application4, R.string.apply_sample, R.color.sample),
    // 2018/2/26新增8大類
    SPECIAL_PRICE(9, R.mipmap.common_special, R.string.apply_special_price, R.color
            .special_price),
    PRODUCTION(10, R.mipmap.common_mass_production, R.string.apply_production, R.color.production),
    NON_STANDARD_INQUIRY(11, R.mipmap.common_inquiry, R.string.apply_non_standard_inquiry, R
            .color.non_standard_inquiry),
    SPRING_RING_INQUIRY(12, R.mipmap.common_spring, R.string.apply_spring_ring_inquiry, R
            .color.spring_ring_inquiry),
    SPECIAL_SHIP(13, R.mipmap.common_dispatch, R.string.apply_special_ship, R.color
            .special_ship),
    REPAYMENT(14, R.mipmap.common_repay, R.string.apply_repayment, R.color.repayment),
    EXPRESS(15, R.mipmap.common_shipping, R.string.apply_express, R.color.express),
    TRAVEL(16, R.mipmap.common_invite, R.string.apply_travel, R.color.travel);

    int value, icon, title, color;

    TripType(int value, int icon, int title, int color) {
        this.value = value;
        this.icon = icon;
        this.title = title;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public int getValue() {
        return value;
    }

    public static TripType getTripTypeByValue(int value) {
        for (TripType tripType : TripType.values()) {
            if (tripType.getValue() == value) {
                return tripType;
            }
        }
        return null;
    }
}
