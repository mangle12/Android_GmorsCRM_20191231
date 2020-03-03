package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "DepartmentSalesOpportunitySub")
public class DepartmentSalesOpportunitySub extends BaseSetting {
    @NonNull
    @PrimaryKey
    public String id;

    public String department_sales_opportunity_id;// 銷售機會設定ID
    public int stage;// 大類
    public String name;// 小項名稱
    public int percentage;// 小項%

    public DepartmentSalesOpportunitySub() {
        super();
        id = "";
    }
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartment_sales_opportunity_id() {
        return department_sales_opportunity_id;
    }

    public void setDepartment_sales_opportunity_id(String department_sales_opportunity_id) {
        this.department_sales_opportunity_id = department_sales_opportunity_id;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
