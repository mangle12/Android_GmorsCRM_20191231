package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunitySub;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

@Entity(tableName = "SalesOpportunity")
public class SalesOpportunity extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String project_id;// 工作項目ID
    public String user_id;// 新建業務的ID
    public String name;// 名稱
    public String description;// 備註
    public String department_sales_opportunity;// 設定ID
    public int percentage;// %
    public int stage;// 銷售階段

    /*2018/1/26新增*/
    public String sales_opportunity_lose_type;// 輸單原因
    public String sales_opportunity_win_type;// 贏單原因
    public String decision_elements;// 決策要素

    /*2018/3/6新增*/
    public String reason;// 原因

    public SalesOpportunity() {
        super();
        id = "";
        name = "";
        description = "";
        reason = "";
        percentage = 0;
        stage = ProjectStatus.START.getValue();
        sales_opportunity_lose_type = "";
        sales_opportunity_win_type = "";
        decision_elements = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DepartmentSalesOpportunity getDepartment_sales_opportunity() {
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING)
                .create();
        try {
            DepartmentSalesOpportunity opportunity = gson.fromJson(department_sales_opportunity,
                    DepartmentSalesOpportunity.class);
            return opportunity;
        } catch (Exception e) {
            return null;
        }
    }

    public void setDepartment_sales_opportunity(DepartmentSalesOpportunity
                                                        department_sales_opportunity) {
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING)
                .create();
        this.department_sales_opportunity = gson.toJson(department_sales_opportunity);
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public ProjectStatus getStage() {
        return ProjectStatus.getProjectStatusByCode(stage);
    }

    public void setStage(ProjectStatus stage) {
        this.stage = stage.getValue();
    }

    public String getDecision_elements() {
        return decision_elements;
    }

    public void setDecision_elements(String decision_elements) {
        this.decision_elements = decision_elements;
    }

    public String getSales_opportunity_lose_type() {
        return sales_opportunity_lose_type;
    }

    public void setSales_opportunity_lose_type(String sales_opportunity_lose_type) {
        this.sales_opportunity_lose_type = sales_opportunity_lose_type;
    }

    public String getSales_opportunity_win_type() {
        return sales_opportunity_win_type;
    }

    public void setSales_opportunity_win_type(String sales_opportunity_win_type) {
        this.sales_opportunity_win_type = sales_opportunity_win_type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
