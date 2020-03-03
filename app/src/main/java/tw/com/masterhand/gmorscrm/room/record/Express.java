package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.AbsentType;
import tw.com.masterhand.gmorscrm.enums.ExpressType;

@Entity(tableName = "Express")
public class Express extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String code_number;// 號碼
    public String trip_id;// 行事曆ID
    public int type;// 0:免費樣品 1:有金額產品(必選)
    public String contract_number;// 合同号：（必填）
    public String model;// 品号/數量：（必填）
    public float amount;// 产品金额：（必填）
    public float expense;// 快递费用（人民币）：（必填）
    public String destination;// 快递目的地：（必填）
    public String reason;// 申请原因：（必填）
    /*2018/5/3新增欄位*/
    public String product_category_id;// 產品大類：（必填）
    public String origin;// 產地：（必填）
    public int arrival_payment;// 運費是否到付：（必填）
    public Date repayment_date;// 預計回款日期：（必填）

    public Express() {
        super();
        id = "";
        code_number = "";
        type = -1;
        contract_number = "";
        model = "";
        amount = 0F;
        expense = 0F;
        destination = "";
        reason = "";
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

    public ExpressType getType() {
        return ExpressType.getTypeByCode(type);
    }

    public void setType(ExpressType type) {
        this.type = type.getCode();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getContract_number() {
        return contract_number;
    }

    public void setContract_number(String contract_number) {
        this.contract_number = contract_number;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getExpense() {
        return expense;
    }

    public void setExpense(float expense) {
        this.expense = expense;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getProduct_category_id() {
        return product_category_id;
    }

    public void setProduct_category_id(String product_category_id) {
        this.product_category_id = product_category_id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getArrival_payment() {
        return arrival_payment;
    }

    public void setArrival_payment(int arrival_payment) {
        this.arrival_payment = arrival_payment;
    }

    public Date getRepayment_date() {
        return repayment_date;
    }

    public void setRepayment_date(Date repayment_date) {
        this.repayment_date = repayment_date;
    }
}
