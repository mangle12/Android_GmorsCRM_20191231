package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.room.record.SampleProduct;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductReport;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleProductType;

public class SampleProductWithConfig {
    SampleProduct product;
    ConfigQuotationProductCategory category;
    ConfigSampleProductType type;
    List<ConfigQuotationProductReport> reports;

    public SampleProductWithConfig() {
        product = new SampleProduct();
        reports = new ArrayList<>();
    }

    public SampleProduct getProduct() {
        return product;
    }

    public void setProduct(SampleProduct product) {
        this.product = product;
    }

    public ConfigQuotationProductCategory getCategory() {
        return category;
    }

    public void setCategory(ConfigQuotationProductCategory category) {
        this.category = category;
    }

    public ConfigSampleProductType getType() {
        return type;
    }

    public void setType(ConfigSampleProductType type) {
        this.type = type;
    }

    public List<ConfigQuotationProductReport> getReports() {
        return reports;
    }
}
