package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.AbsentType;

@Entity(tableName = "Absent")
public class Absent extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String trip_id;// 行事曆ID
    public int type;// 請假類型
    public String reason;// 原因

    public Absent() {
        super();
        id = "";
        type = AbsentType.SICK.getCode();
        reason = "";
    }
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public AbsentType getType() {
        return AbsentType.getTypeByCode(type);
    }

    public void setType(AbsentType type) {
        this.type = type.getCode();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
