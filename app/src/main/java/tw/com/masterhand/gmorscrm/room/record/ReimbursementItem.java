package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.ReimbursementType;
import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

@Entity(tableName = "ReimbursementItem")
public class ReimbursementItem extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 報銷單ID
    public String config_reimbursement_item_id;// 項目ID
    public float amount;// 金額
    public Date from_date;// 發生日期
    public Date to_date;// 結束日期
    public int type;// 扣除種類 0:尚未選擇 1:自己 2:部門
    public String department_id;// 部門ID
    public String customer_id;// 客戶ID
    public String together;// 同行人
    public String description;// 備註


    public ReimbursementItem() {
        super();
        id = "";
        from_date = new Date();
        to_date = new Date();
        amount = 0;
        type = 0;
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

    public String getConfig_reimbursement_item_id() {
        return config_reimbursement_item_id;
    }

    public void setConfig_reimbursement_item_id(String config_reimbursement_item_id) {
        this.config_reimbursement_item_id = config_reimbursement_item_id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getFrom_date() {
        return from_date;
    }

    public void setFrom_date(Date from_date) {
        this.from_date = from_date;
    }

    public Date getTo_date() {
        return to_date;
    }

    public void setTo_date(Date to_date) {
        this.to_date = to_date;
    }

    public ReimbursementType getType() {
        return ReimbursementType.getReimbursementTypeByValue(type);
    }

    public void setType(ReimbursementType type) {
        this.type = type.getValue();
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTogether() {
        return together;
    }

    public void setTogether(String together) {
        this.together = together;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
