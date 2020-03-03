package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.ParentType;

@Entity(tableName = "Contacter")
public class Contacter extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 任務id
    public String user_id;// 客戶聯絡人id
    public int parent_type;

    public Contacter() {
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

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ParentType getParent_type() {
        return ParentType.getTypeByValue(parent_type);
    }

    public void setParent_type(ParentType parent_type) {
        this.parent_type = parent_type.getValue();
    }
}
