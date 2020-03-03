package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.SpecialPriceApproval;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceProjectStatus;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceType;
import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

@Entity(tableName = "SpecialShip")
public class SpecialShip extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;
    //↓財務填寫欄位
    public String code_number;// 號碼
    public String trip_id;// 行事曆ID
    public String ship_code;// 出货单号：必填
    public float ship_amount;// 出货金额：必填
    public float not_billing_amount;// 已出货未开票金额：浮点类型，必填
    public float not_expire_amount;// 已开票未到期金额：浮点类型，必填
    public float expire_day;// 最近到期天数：必填
    public float expire_amount;// 逾期总金额：必填
    public float biggest_expire_day;// 最长逾期天数：必填
    public float prepayment;// 预付款金额（负数的绝对值）：必填
    public float credit;// 获批信用额度：必填
    public float credit_risk;// 信贷风险总额：选填
    public int less_than_500;// 是否小于500元：必填。0:否 1:是
    public String user_id;// 業務ID（必填）
    //↓銷售填寫欄位
    public Date repayment_date;// 预计回款日期：必填
    public int ship_again;// 预计回款日期前是否再次发货：必填。0:否 1:是


    public SpecialShip() {
        super();
        id = "";
        code_number = "";
        trip_id = "";
        ship_code = "";
        ship_amount = 0F;
        not_billing_amount = 0F;
        not_expire_amount = 0F;
        expire_day = 0F;
        expire_amount = 0F;
        biggest_expire_day = 0F;
        prepayment = 0F;
        credit = 0F;
        credit_risk = 0F;
        less_than_500 = -1;
        user_id = "";
        ship_again = -1;
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

    public String getShip_code() {
        return ship_code;
    }

    public void setShip_code(String ship_code) {
        this.ship_code = ship_code;
    }

    public float getShip_amount() {
        return ship_amount;
    }

    public void setShip_amount(float ship_amount) {
        this.ship_amount = ship_amount;
    }

    public float getNot_billing_amount() {
        return not_billing_amount;
    }

    public void setNot_billing_amount(float not_billing_amount) {
        this.not_billing_amount = not_billing_amount;
    }

    public float getNot_expire_amount() {
        return not_expire_amount;
    }

    public void setNot_expire_amount(float not_expire_amount) {
        this.not_expire_amount = not_expire_amount;
    }

    public float getExpire_day() {
        return expire_day;
    }

    public void setExpire_day(float expire_day) {
        this.expire_day = expire_day;
    }

    public float getExpire_amount() {
        return expire_amount;
    }

    public void setExpire_amount(float expire_amount) {
        this.expire_amount = expire_amount;
    }

    public float getBiggest_expire_day() {
        return biggest_expire_day;
    }

    public void setBiggest_expire_day(float biggest_expire_day) {
        this.biggest_expire_day = biggest_expire_day;
    }

    public float getPrepayment() {
        return prepayment;
    }

    public void setPrepayment(float prepayment) {
        this.prepayment = prepayment;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public float getCredit_risk() {
        return credit_risk;
    }

    public void setCredit_risk(float credit_risk) {
        this.credit_risk = credit_risk;
    }

    public int getLess_than_500() {
        return less_than_500;
    }

    public void setLess_than_500(int less_than_500) {
        this.less_than_500 = less_than_500;
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

    public int getShip_again() {
        return ship_again;
    }

    public void setShip_again(int ship_again) {
        this.ship_again = ship_again;
    }

    public float getTotal() {
        return ship_amount + not_billing_amount + not_expire_amount - prepayment;
    }

    public float getExceed_quota() {
        return getTotal() - credit;
    }

}
