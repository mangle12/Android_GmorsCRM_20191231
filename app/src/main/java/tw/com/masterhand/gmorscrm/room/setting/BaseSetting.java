package tw.com.masterhand.gmorscrm.room.setting;

import java.util.Date;

import tw.com.masterhand.gmorscrm.room.converter.DateConverter;

public class BaseSetting {

    public Date created_at;
    public Date updated_at;
    public Date deleted_at;

    public BaseSetting() {
        created_at = new Date();
        updated_at = new Date();
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

}
