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

@Entity(tableName = "SampleProduct")
public class SampleProduct extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 樣品ID
    public String product_category_id;// 產品大類 ID (參考ConfigQuotationProductCategory)
    public String product_type_id;// 產品種類 ID
    public String product_model;// 產品編號
    public float qty_apply;// 申請數量
    public float qty_annual;// 年用量
    public String product_report_id;// 產品所需報告id (參考ConfigQuotationProductReport)
    public String description;// 產品描述
    public int inventory_supplier;// 供應商是否有庫存
    public int inventory_factory;// 胤舜是否有庫存
    public float amount;// 送樣單價
    public float total;// 總計

    public SampleProduct() {
        super();
        id = "";
        parent_id = "";
        product_category_id = "";
        product_report_id = "";
        product_type_id = "";
        product_model = "";
        qty_apply = 0;
        qty_annual = 0;
        inventory_supplier = 0;
        inventory_factory = 0;
        description = "";
        amount = 0;
        total = 0;
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

    public String getProduct_category_id() {
        return product_category_id;
    }

    public void setProduct_category_id(String product_category_id) {
        this.product_category_id = product_category_id;
    }

    public String getProduct_type_id() {
        return product_type_id;
    }

    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    public String getProduct_model() {
        return product_model;
    }

    public void setProduct_model(String product_model) {
        this.product_model = product_model;
    }

    public float getQty_apply() {
        return qty_apply;
    }

    public void setQty_apply(float qty_apply) {
        this.qty_apply = qty_apply;
        total = qty_apply * amount;
    }

    public float getQty_annual() {
        return qty_annual;
    }

    public void setQty_annual(float qty_annual) {
        this.qty_annual = qty_annual;
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

    public int getInventory_supplier() {
        return inventory_supplier;
    }

    public void setInventory_supplier(int inventory_supplier) {
        this.inventory_supplier = inventory_supplier;
    }

    public int getInventory_factory() {
        return inventory_factory;
    }

    public void setInventory_factory(int inventory_factory) {
        this.inventory_factory = inventory_factory;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
        total = qty_apply * amount;
    }

    public float getTotal() {
        return qty_apply * amount;
    }

}
