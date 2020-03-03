package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "SpringRingInquiry")
public class SpringRingInquiry extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String trip_id;// 行事曆ID
    public String code_number;// 询价单号：必填，采购填写权限
    public String name_english;// 英文简称+项目名称：必填
    public String year_potential_id;// 全年潜在销售额ID：必填
    public int need_image;// 是否需要应用部出图：必填。0:否 1:是
    public String product_type_id;// 产品类型ID：必填
    public String apply;// 应用及位置：必填
    public String competitive_status_id;// 竞争状态ID：必填
    public String sgb_advantage_id;// SGB优势：必填
    public String sgb_disadvantage_id;// SGB劣势：必填
    public String trend_id;// 趋势ID：必填
    public int need_parity;// 是否需要比价：必填。0:否 1:是
    public String sealed_state;// 密封状态ID：必填
    public String speed;// 运动速度M/S或转/分：必填
    public String pressure;// 工作压力区间：必填
    public String temperature;// 工作温度区间：必填
    public String lifetime;// 寿命要求：必填
    public String other;// 其他（轴孔不对中、轴跳、安装方式等）：必填
    public String static_a_material;// 静态表面A材质：必填
    public String static_a_hardness;// 静态表面A硬度：必填
    public String static_a_smoothness;// 静态表面A表面处理及光洁度：必填
    public String dynamic_b_material;// 动态表面B材质：必填
    public String dynamic_b_hardness;// 动态表面B硬度：必填
    public String dynamic_b_smoothness;// 动态表面B表面处理及光洁度：必填


    public SpringRingInquiry() {
        super();
        id = "";
        code_number = "";
        name_english = "";
        year_potential_id = "";
        need_image = -1;
        product_type_id = "";
        apply = "";
        competitive_status_id = "";
        sgb_advantage_id = "";
        sgb_disadvantage_id = "";
        trend_id = "";
        need_parity = -1;
        sealed_state = "";
        speed = "";
        pressure = "";
        temperature = "";
        lifetime = "";
        other = "";
        static_a_material = "";
        static_a_hardness = "";
        static_a_smoothness = "";
        dynamic_b_material = "";
        dynamic_b_hardness = "";
        dynamic_b_smoothness = "";
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

    public String getName() {
        return name_english;
    }

    public void setName(String name) {
        this.name_english = name;
    }

    public String getYear_potential_id() {
        return year_potential_id;
    }

    public void setYear_potential_id(String year_potential_id) {
        this.year_potential_id = year_potential_id;
    }

    public int getNeed_image() {
        return need_image;
    }

    public void setNeed_image(int need_image) {
        this.need_image = need_image;
    }

    public String getProduct_type_id() {
        return product_type_id;
    }

    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getCompetitive_status_id() {
        return competitive_status_id;
    }

    public void setCompetitive_status_id(String competitive_status_id) {
        this.competitive_status_id = competitive_status_id;
    }

    public String getSgb_advantage_id() {
        return sgb_advantage_id;
    }

    public void setSgb_advantage_id(String sgb_advantage_id) {
        this.sgb_advantage_id = sgb_advantage_id;
    }

    public String getSgb_disadvantage_id() {
        return sgb_disadvantage_id;
    }

    public void setSgb_disadvantage_id(String sgb_disadvantage_id) {
        this.sgb_disadvantage_id = sgb_disadvantage_id;
    }

    public String getTrend_id() {
        return trend_id;
    }

    public void setTrend_id(String trend_id) {
        this.trend_id = trend_id;
    }

    public int getNeed_parity() {
        return need_parity;
    }

    public void setNeed_parity(int need_parity) {
        this.need_parity = need_parity;
    }

    public String getSealed_state() {
        return sealed_state;
    }

    public void setSealed_state(String sealed_state) {
        this.sealed_state = sealed_state;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getLifetime() {
        return lifetime;
    }

    public void setLifetime(String lifetime) {
        this.lifetime = lifetime;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getStatic_a_material() {
        return static_a_material;
    }

    public void setStatic_a_material(String static_a_material) {
        this.static_a_material = static_a_material;
    }

    public String getStatic_a_hardness() {
        return static_a_hardness;
    }

    public void setStatic_a_hardness(String static_a_hardness) {
        this.static_a_hardness = static_a_hardness;
    }

    public String getStatic_a_smoothness() {
        return static_a_smoothness;
    }

    public void setStatic_a_smoothness(String static_a_smoothness) {
        this.static_a_smoothness = static_a_smoothness;
    }

    public String getDynamic_b_material() {
        return dynamic_b_material;
    }

    public void setDynamic_b_material(String dynamic_b_material) {
        this.dynamic_b_material = dynamic_b_material;
    }

    public String getDynamic_b_hardness() {
        return dynamic_b_hardness;
    }

    public void setDynamic_b_hardness(String dynamic_b_hardness) {
        this.dynamic_b_hardness = dynamic_b_hardness;
    }

    public String getDynamic_b_smoothness() {
        return dynamic_b_smoothness;
    }

    public void setDynamic_b_smoothness(String dynamic_b_smoothness) {
        this.dynamic_b_smoothness = dynamic_b_smoothness;
    }
}
