package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "SpecialPriceProduct")
public class SpecialPriceProduct extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 特價ID
    public float qty;// 數量（必填）
    public float amount;// 產品單價（必填）
    public String product_category_id;// 產品大類id（必填）
    public String description;// 产品型号/材质/规格及说明（必填）
    public String remark;// 備註
    public float discount;// 折扣（必填）
    public float competitor_amount;// 竞争对手参考价
    public String product_category_sub_id;// 產品分類


    public SpecialPriceProduct() {
        super();
        id = "";
        parent_id = "";
        qty = 0;
        amount = 0;
        product_category_id = "";
        product_category_sub_id = "";
        description = "";
        remark = "";
        discount = 0;
        competitor_amount = 0;
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

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getProduct_category_id() {
        return product_category_id;
    }

    public void setProduct_category_id(String product_category_id) {
        this.product_category_id = product_category_id;
    }

    public String getProduct_category_sub_id() {
        return product_category_sub_id;
    }

    public void setProduct_category_sub_id(String product_category_sub_id) {
        this.product_category_sub_id = product_category_sub_id;
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

    public float getCompetitor_amount() {
        return competitor_amount;
    }

    public void setCompetitor_amount(float competitor_amount) {
        this.competitor_amount = competitor_amount;
    }
}
