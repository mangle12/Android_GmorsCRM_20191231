package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import tw.com.masterhand.gmorscrm.enums.ExpressType;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceApproval;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceProjectStatus;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceType;

@Entity(tableName = "SpecialPrice")
public class SpecialPrice extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String code_number;// 號碼
    public String trip_id;// 行事曆ID
    public int project_status;// 项目状态：（必填） 0:试样 1:小批量 2:量产
    public int type;// 特价类型：（必填）0:單次 1:長期
    public String reason;// 申请原因：（必填）
    public float year_amount;// 项目预计年采购金额
    public float year_discount;// 项目年用量平均折扣
    public String offer;// 特价产品OFFER编号
    public int approval;// 1:一級審批 2:二級審批


    public SpecialPrice() {
        super();
        id = "";
        code_number = "";
        project_status = -1;
        type = -1;
        reason = "";
        year_amount = 0F;
        year_discount = 0F;
        offer = "";
        approval = -1;
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

    public SpecialPriceProjectStatus getProject_status() {
        return SpecialPriceProjectStatus.getTypeByCode(project_status);
    }

    public void setProject_status(SpecialPriceProjectStatus project_status) {
        this.project_status = project_status.getCode();
    }

    public SpecialPriceType getType() {
        return SpecialPriceType.getTypeByCode(type);
    }

    public void setType(SpecialPriceType type) {
        this.type = type.getCode();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public float getYear_amount() {
        return year_amount;
    }

    public void setYear_amount(float year_amount) {
        this.year_amount = year_amount;
    }

    public float getYear_discount() {
        return year_discount;
    }

    public void setYear_discount(float year_discount) {
        this.year_discount = year_discount;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public SpecialPriceApproval getApproval() {
        return SpecialPriceApproval.getTypeByCode(approval);
    }

    public void setApproval(SpecialPriceApproval approval) {
        this.approval = approval.getCode();
    }
}
