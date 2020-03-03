package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import tw.com.masterhand.gmorscrm.enums.SpecialPriceApproval;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceProjectStatus;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceType;
import tw.com.masterhand.gmorscrm.model.Address;

@Entity(tableName = "Travel")
public class Travel extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String code_number;// 號碼
    public String trip_id;// 行事曆ID
    public String address;// 出差地（必填）JSON
    public String info;// 拜访人员信息（必填）
    public String reason;// 拜访目的（必填）
    public String support;// 所需支持（必填）
    public String customer_background;// 客户背景信息
    public String customer_tech;// 客户技术信息


    public Travel() {
        super();
        id = "";
        code_number = "";
        address = "";
        info = "";
        reason = "";
        support = "";
        customer_background = "";
        customer_tech = "";
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

    public Address getAddress() {
        if (TextUtils.isEmpty(address))
            return new Address();
        return new Gson().fromJson(address, Address.class);
    }

    public void setAddress(Address address) {
        if (address != null)
            this.address = new Gson().toJson(address);
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getCustomer_background() {
        return customer_background;
    }

    public void setCustomer_background(String customer_background) {
        this.customer_background = customer_background;
    }

    public String getCustomer_tech() {
        return customer_tech;
    }

    public void setCustomer_tech(String customer_tech) {
        this.customer_tech = customer_tech;
    }
}
