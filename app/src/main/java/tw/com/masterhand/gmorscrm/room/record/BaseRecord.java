package tw.com.masterhand.gmorscrm.room.record;

import java.util.Date;

import tw.com.masterhand.gmorscrm.room.converter.DateConverter;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class BaseRecord {

    public Date created_at;
    public Date updated_at;
    public Date deleted_at;

    public int syncable;
    public String code;

    public BaseRecord() {
        created_at = new Date();
        updated_at = new Date();
        syncable = 1;
        code = "";
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Date deleted_at) {
        this.deleted_at = deleted_at;
    }

    public boolean getSyncable() {
        return syncable == 1;
    }

    public String getCode() {
        return code;
    }
}
