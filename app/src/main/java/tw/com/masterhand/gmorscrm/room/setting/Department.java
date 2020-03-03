package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "Department")
public class Department extends BaseSetting {
    @NonNull
    @PrimaryKey
    public String id;

    public String company_id;// 公司ID
    public String name;// 部門名稱
    public String manager_id;// 部門經理ID
    public float expense_quota; // 差旅費額度
    public float expense_left; // 差旅費剩餘

    public Department() {
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

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManager_id() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id = manager_id;
    }

    public float getExpense_quota() {
        return expense_quota;
    }

    public void setExpense_quota(float expense_quota) {
        this.expense_quota = expense_quota;
    }

    public float getExpense_left() {
        return expense_left;
    }

    public void setExpense_left(float expense_left) {
        this.expense_left = expense_left;
    }
}
