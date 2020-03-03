package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunitySub;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

@Entity(tableName = "SalesOpportunitySub")
public class SalesOpportunitySub extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String sales_opportunity_id;// 銷售機會ID
    public String department_sales_opportunity_sub;// 銷售機會設定
    public int selected;// 是否選擇


    public SalesOpportunitySub() {
        super();
        id = "";
        selected = 0;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSales_opportunity_id() {
        return sales_opportunity_id;
    }

    public void setSales_opportunity_id(String sales_opportunity_id) {
        this.sales_opportunity_id = sales_opportunity_id;
    }

    public DepartmentSalesOpportunitySub getDepartment_sales_opportunity() {
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING)
                .create();
        try {
            DepartmentSalesOpportunitySub sub = gson.fromJson(department_sales_opportunity_sub,
                    DepartmentSalesOpportunitySub.class);
            return sub;
        } catch (Exception e) {
            Logger.e(getClass().getSimpleName(), "Exception:" + e.getMessage() + "\n" +
                    department_sales_opportunity_sub);
            return null;
        }
    }

    public void setDepartment_sales_opportunity(DepartmentSalesOpportunitySub
                                                        department_sales_opportunity) {
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING)
                .create();
        this.department_sales_opportunity_sub = gson.toJson(department_sales_opportunity);
    }

    public boolean getSelected() {
        return selected == 1;
    }

    public void setSelected(boolean selected) {
        this.selected = selected ? 1 : 0;
    }
}
