package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import tw.com.masterhand.gmorscrm.enums.SpecialPriceApproval;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceProjectStatus;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceType;

@Entity(tableName = "NonStandardInquiry")
public class NonStandardInquiry extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String code_number;// 號碼
    public String trip_id;// 行事曆ID
    public String office_id;// 销售组织ID：必填
    public int need_parity;// 是否需要比价：必填。0:否 1:是
    public String note;// 特殊要求：选填


    public NonStandardInquiry() {
        super();
        id = "";
        code_number = "";
        office_id = "";
        need_parity = -1;
        note = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode_number() {
        return code_number;
    }

    public void setCode_number(String code) {
        this.code_number = code;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getOffice_id() {
        return office_id;
    }

    public void setOffice_id(String office_id) {
        this.office_id = office_id;
    }

    public int getNeed_parity() {
        return need_parity;
    }

    public void setNeed_parity(int need_parity) {
        this.need_parity = need_parity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
