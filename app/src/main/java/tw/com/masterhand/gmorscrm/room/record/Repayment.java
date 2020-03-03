package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.ReimbursementType;
import tw.com.masterhand.gmorscrm.enums.RepaymentType;
import tw.com.masterhand.gmorscrm.model.Address;
import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

@Entity(tableName = "Repayment")
public class Repayment extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String code_number;// 號碼
    public String trip_id;// 行事曆ID
    public String user_id;// 業務ID（必填）
    public Date repayment_date;// 回款日期（必填）
    public float amount;// 回款金額（必填）
    public int type;// 回款類型 0:電匯 1:匯票（必填）


    public Repayment() {
        super();
        id = "";
        code_number = "";
        user_id = "";
        amount = 0F;
        type = -1;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getRepayment_date() {
        return repayment_date;
    }

    public void setRepayment_date(Date repayment_date) {
        this.repayment_date = repayment_date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public RepaymentType getType() {
        return RepaymentType.getTypeByCode(type);
    }

    public void setType(RepaymentType type) {
        this.type = type.getCode();
    }
}
