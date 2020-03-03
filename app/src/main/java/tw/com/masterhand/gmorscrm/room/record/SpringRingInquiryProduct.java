package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

@Entity(tableName = "SpringRingInquiryProduct")
public class SpringRingInquiryProduct extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 彈簧蓄能圈詢價ID
    public String equipment;// 設備或者應用名稱
    public int back_pressure;// 是否有背壓
    public String part_number;// 零件号或图号：必填
    public String sealed_type;// 密封沟槽类型：必填
    public String product_type;// 产品类型（如530等）：必填
    public String groove_diameter;// 沟槽内径：必填
    public String groove_outer_diameter;// 沟槽外径：必填
    public String groove_width;// 沟槽宽度：必填
    public String material_number;// 材质编号（如A0205）：必填
    public String other;// 其它说明(如阀杆、阀座)：选填
    public String count;// 询价数量：必填
    public Date delivery_date;// 交期：必填
    public String remark;// 备注（特殊要求，如出图)：选填
    public String medium;// 介質及沖程：必填


    public SpringRingInquiryProduct() {
        super();
        id = "";
        parent_id = "";
        equipment = "";
        back_pressure = -1;
        part_number = "";
        sealed_type = "";
        product_type = "";
        groove_diameter = "";
        groove_outer_diameter = "";
        groove_width = "";
        material_number = "";
        other = "";
        count = "";
        delivery_date = null;
        remark = "";
        medium = "";
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

    public String getPart_number() {
        return part_number;
    }

    public void setPart_number(String part_number) {
        this.part_number = part_number;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public int getBack_pressure() {
        return back_pressure;
    }

    public void setBack_pressure(int back_pressure) {
        this.back_pressure = back_pressure;
    }

    public String getSealed_type() {
        return sealed_type;
    }

    public void setSealed_type(String sealed_type) {
        this.sealed_type = sealed_type;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getGroove_diameter() {
        return groove_diameter;
    }

    public void setGroove_diameter(String groove_diameter) {
        this.groove_diameter = groove_diameter;
    }

    public String getGroove_outer_diameter() {
        return groove_outer_diameter;
    }

    public void setGroove_outer_diameter(String groove_outer_diameter) {
        this.groove_outer_diameter = groove_outer_diameter;
    }

    public String getGroove_width() {
        return groove_width;
    }

    public void setGroove_width(String groove_width) {
        this.groove_width = groove_width;
    }

    public String getMaterial_number() {
        return material_number;
    }

    public void setMaterial_number(String material_number) {
        this.material_number = material_number;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Date getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(Date delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }
}
