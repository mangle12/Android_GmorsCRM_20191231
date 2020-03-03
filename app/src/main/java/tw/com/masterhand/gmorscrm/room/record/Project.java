package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

@Entity(tableName = "Project")
public class Project extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String name;// 工作項目名稱
    public String user_id;// 業務ID
    public String manager_id;// 主管ID
    public String customer_id;// 客戶公司ID
    public String customer_department_name;// 客戶部門
    public Date from_date;// 起始時間
    public float expect_amount;// 預估金額
    public String description;// 備註
    public String currency_id;// 貨幣ID
    public String currency_rate_id;// 匯率ID

    /*2018/2/7新增*/
    public String product_category_id;// 產品類型
    public Date check_date;// 結單日期

    /*2018/3/6新增*/
    public String sales_opportunity_type;// 機會類型
    public String department_project_source;// 機會來源

    public Project() {
        super();
        id = "";
        manager_id = "";
        currency_id = "";
        customer_department_name = "";
        product_category_id = "";
        expect_amount = 0;
        description = "";
        sales_opportunity_type = "";
        department_project_source = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getManager_id() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id = manager_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_department_name() {
        return customer_department_name;
    }

    public void setCustomer_department_name(String customer_department_name) {
        this.customer_department_name = customer_department_name;
    }

    public Date getFrom_date() {
        return from_date;
    }

    public void setFrom_date(Date from_date) {
        this.from_date = from_date;
    }

    public float getExpect_amount() {
        return expect_amount;
    }

    public void setExpect_amount(float expect_amount) {
        this.expect_amount = expect_amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getCurrency_rate_id() {
        return currency_rate_id;
    }

    public void setCurrency_rate_id(String currency_rate_id) {
        this.currency_rate_id = currency_rate_id;
    }

    public List<String> getProduct_category_id() {
        if (TextUtils.isEmpty(product_category_id)) {
            return new ArrayList<>();
        } else {
            return Arrays.asList(StringUtils.split(product_category_id, ","));
        }
    }

    public void setProduct_category_id(List<String> product_category_id) {
        if (product_category_id.size() == 0) {
            this.product_category_id = "";
        } else {
            this.product_category_id = StringUtils.join(product_category_id, ",");
        }
    }

    public Date getCheck_date() {
        return check_date;
    }

    public void setCheck_date(Date check_date) {
        this.check_date = check_date;
    }

    public String getDepartment_project_source() {
        return department_project_source;
    }

    public void setDepartment_project_source(String department_project_source) {
        this.department_project_source = department_project_source;
    }

    public String getSales_opportunity_type() {
        return sales_opportunity_type;
    }

    public void setSales_opportunity_type(String sales_opportunity_type) {
        this.sales_opportunity_type = sales_opportunity_type;
    }
}
