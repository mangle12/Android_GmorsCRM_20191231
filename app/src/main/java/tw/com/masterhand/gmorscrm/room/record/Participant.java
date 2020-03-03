package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.enums.ParentType;
import tw.com.masterhand.gmorscrm.enums.SignStatus;
import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

@Entity(tableName = "Participant")
public class Participant extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 任務(工作項目)id
    public String user_id;// 業務id
    public int accept;// 是否參加 0:未選 1: 不參加 2:可能 3:參加
    public int status;// 簽到 0:未簽到 1:已簽到 2:已簽退
    public Date sign_in_at;// 簽到時間
    public Date sign_out_at;// 簽退時間
    public String trip_id;// 行事曆ID
    public int parent_type;

    public Participant() {
        super();
        id = "";
        accept = 0;
        status = 0;
        trip_id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public AcceptType getAccept() {
        return AcceptType.getTypeByCode(accept);
    }

    public void setAccept(AcceptType accept) {
        this.accept = accept.getCode();
    }

    public SignStatus getStatus() {
        return SignStatus.getStatusByCode(status);
    }

    public void setStatus(SignStatus status) {
        this.status = status.getCode();
    }

    public Date getSign_in_at() {
        return sign_in_at;
    }

    public void setSign_in_at(Date sign_in_at) {
        this.sign_in_at = sign_in_at;
    }

    public Date getSign_out_at() {
        return sign_out_at;
    }

    public void setSign_out_at(Date sign_out_at) {
        this.sign_out_at = sign_out_at;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public ParentType getParent_type() {
        return ParentType.getTypeByValue(parent_type);
    }

    public void setParent_type(ParentType parent_type) {
        this.parent_type = parent_type.getValue();
    }
}
