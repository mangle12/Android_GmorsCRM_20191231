package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity(tableName = "QuotationProduct")
public class QuotationProduct extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 報價ID
    public float qty;// 數量
    public float amount;// 產品單價
    public String product_category_id;// 產品大類id
    public String product_model;// 產品品號
    public String product_report_id;// 所需報告id
    public String description;// 產品描述
    public String remark;// 備註
    public float discount;// 折扣
    public String non_standard_number;// OFFER號

    public QuotationProduct() {
        super();
        id = "";
        product_category_id = "";
        product_report_id = "";
        qty = 0;
        amount = 0;
        discount = 1;
        product_model = "";
        remark = "";
        non_standard_number = "";
    }

    public float getTotal() {
        float realDiscount = discount;
        if (realDiscount == 0)
            realDiscount = 1;
        float total = amount * realDiscount * qty;
        return total < 0 ? 0 : total;
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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public String getProduct_category_id() {
        return product_category_id;
    }

    public void setProduct_category_id(String product_category_id) {
        this.product_category_id = product_category_id;
    }

    public String getProduct_model() {
        return product_model;
    }

    public void setProduct_model(String product_model) {
        this.product_model = product_model;
    }

    public List<String> getProduct_report_id() {
        if (TextUtils.isEmpty(product_report_id)) {
            return new ArrayList<>();
        } else {
            return Arrays.asList(StringUtils.split(product_report_id, ","));
        }
    }

    public void setProduct_report_id(List<String> product_report_id) {
        if (product_report_id.size() == 0) {
            this.product_report_id = "";
        } else {
            this.product_report_id = StringUtils.join(product_report_id, ",");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getNon_standard_number() {
        return non_standard_number;
    }

    public void setNon_standard_number(String non_standard_number) {
        this.non_standard_number = non_standard_number;
    }
}
