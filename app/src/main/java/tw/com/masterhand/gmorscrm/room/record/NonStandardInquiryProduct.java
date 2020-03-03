package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "NonStandardInquiryProduct")
public class NonStandardInquiryProduct extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 非標詢價ID
    public String code_number;// 产品编码：选填
    public String product_category_id;// 產品大類id：必填
    public String name;// 产品名称（小类）：必填
    public String size;// 尺寸：选填
    public String tolerance;// 公差要求：选填
    public String material;// 材料：选填
    public String hardness;// 硬度(SHORE or  IRHD)：选填
    public String color;// 颜色：选填F
    public String certification;// 认证要求：选填
    public String wrapping;// 包装要求：选填
    public String reference_number;// 参考图号：选填
    public String medium;// 介质：选填
    public String temperature;// 温度：选填
    public String pressure;// 压力：选填
    public String status;// 动态/静态：选填
    public String speed;// 线速度：选填
    public String count;// 询价数量：必填
    public float year_count;// 年用量：选填
    public String remark;// 備註：选填


    public NonStandardInquiryProduct() {
        super();
        id = "";
        parent_id = "";
        code_number = "";
        product_category_id = "";
        name = "";
        size = "";
        tolerance = "";
        material = "";
        hardness = "";
        color = "";
        certification = "";
        wrapping = "";
        reference_number = "";
        medium = "";
        temperature = "";
        pressure = "";
        status = "";
        speed = "";
        count = "";
        year_count = 0;
        remark = "";
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

    public String getCode_number() {
        return code_number;
    }

    public void setCode_number(String code) {
        this.code_number = code;
    }

    public String getProduct_category_id() {
        return product_category_id;
    }

    public void setProduct_category_id(String product_category_id) {
        this.product_category_id = product_category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTolerance() {
        return tolerance;
    }

    public void setTolerance(String tolerance) {
        this.tolerance = tolerance;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getHardness() {
        return hardness;
    }

    public void setHardness(String hardness) {
        this.hardness = hardness;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getWrapping() {
        return wrapping;
    }

    public void setWrapping(String wrapping) {
        this.wrapping = wrapping;
    }

    public String getReference_number() {
        return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public float getYear_count() {
        return year_count;
    }

    public void setYear_count(float year_count) {
        this.year_count = year_count;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
