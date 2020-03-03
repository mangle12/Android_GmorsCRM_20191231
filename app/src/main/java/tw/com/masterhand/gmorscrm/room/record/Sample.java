package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;

import tw.com.masterhand.gmorscrm.model.Address;
import tw.com.masterhand.gmorscrm.model.Phone;
import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

@Entity(tableName = "Sample")
public class Sample extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String trip_id;// 行事曆ID
    public String amount_range_id;// 樣品單總面額價金額範圍id
    public String reason_id;// 樣品送樣理由id
    public String source_id;// 樣品送樣來源id
    public String test_cycle;// 測試週期
    public String test_evaluation_basis;// 測試評估依據
    public Date shipping_date;// 發貨日期
    public String import_method_id;// 樣品進口方式id
    public String payment_method_id;// 樣品國內運輸 - 付款方式id
    public String address;// 收貨人地址
    public String receiver;// 收貨人
    public String tel;// 收貨電話
    public String brief;// 簡述

    /*2018/1/26新增*/
    public Date test_from_date;// 測試開始日期

    public Sample() {
        super();
        id = "";
        amount_range_id = "";
        reason_id = "";
        source_id = "";
        import_method_id = "";
        payment_method_id = "";
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

    public String getAmount_range_id() {
        return amount_range_id;
    }

    public void setAmount_range_id(String amount_range_id) {
        this.amount_range_id = amount_range_id;
    }

    public String getReason_id() {
        return reason_id;
    }

    public void setReason_id(String reason_id) {
        this.reason_id = reason_id;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getTest_cycle() {
        return test_cycle;
    }

    public void setTest_cycle(String test_cycle) {
        this.test_cycle = test_cycle;
    }

    public String getTest_evaluation_basis() {
        return test_evaluation_basis;
    }

    public void setTest_evaluation_basis(String test_evaluation_basis) {
        this.test_evaluation_basis = test_evaluation_basis;
    }

    public Date getShipping_date() {
        return shipping_date;
    }

    public void setShipping_date(Date shipping_date) {
        this.shipping_date = shipping_date;
    }

    public String getImport_method_id() {
        return import_method_id;
    }

    public void setImport_method_id(String import_method_id) {
        this.import_method_id = import_method_id;
    }

    public String getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(String payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Date getTest_from_date() {
        return test_from_date;
    }

    public void setTest_from_date(Date test_from_date) {
        this.test_from_date = test_from_date;
    }
}
