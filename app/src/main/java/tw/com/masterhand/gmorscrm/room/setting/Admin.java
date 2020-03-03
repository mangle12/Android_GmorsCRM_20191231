package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "Admin")
public class Admin extends BaseSetting{
    @NonNull
    @PrimaryKey
    public String id;

    public String name;
    public String email;
    public String company_id;
    public String username;
    public String department_id;
    public int status;// 狀態 0:停用 1:啟用
    public int assistant;// 是否為銷售助理 0:否 1:是

    public Admin() {
        super();
        id = "";
        status = 0;
        assistant = 0;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getUsername() {
        return username;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public int getStatus() {
        return status;
    }

    public int getAssistant() {
        return assistant;
    }
}
