package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.AbsentType;
import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

@Entity(tableName = "NewProjectProduction")
public class NewProjectProduction extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String code_number;// 號碼
    public String trip_id;// 行事曆ID
    public String industry_id;// 行業別ID（必填）
    public float year_amount;// 项目潜力（年销售额）：（必填）
    public String model;// 首次批产品号（必填）
    public Date date;// 首次批日期（必填）
    public float amount;// 首次批金额：（必填）
    public String product;// 产品大类：（必填）
    public int psw;// PSW是否签回（汽车行业）：0:否 1:是
    public float discount;// 折扣：（必填）


    public NewProjectProduction() {
        super();
        id = "";
        code_number = "";
        industry_id = "";
        year_amount = 0F;
        model = "";
        amount = 0F;
        product = "";
        psw = -1;
        discount = 0F;
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

    public String getIndustry_id() {
        return industry_id;
    }

    public void setIndustry_id(String industry_id) {
        this.industry_id = industry_id;
    }

    public float getYear_amount() {
        return year_amount;
    }

    public void setYear_amount(float year_amount) {
        this.year_amount = year_amount;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getPsw() {
        return psw;
    }

    public void setPsw(int psw) {
        this.psw = psw;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }
}
