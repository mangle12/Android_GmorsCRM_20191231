package tw.com.masterhand.gmorscrm.model;

import java.util.Date;

import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.FileType;
import tw.com.masterhand.gmorscrm.enums.SampleFileType;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Project;
import tw.com.masterhand.gmorscrm.room.record.Sample;
import tw.com.masterhand.gmorscrm.room.record.Trip;

public class SampleFile {
    public String id;

    public Date created_at;
    public Date updated_at;
    public Date deleted_at;

    public int type;// int 種類 1:3D檔案 2:照片
    public String sample_id;// string 送樣ID
    public String file_url;// string url path
    public String preview_url;// string 3D檔案預覽圖片

    public SampleFile() {
        type = FileType.PHOTO.getValue();
        created_at = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public SampleFileType getType() {
        return SampleFileType.getFileTypeByValue(type);
    }

    public void setType(SampleFileType type) {
        this.type = type.getValue();
    }

    public String getSample_id() {
        return sample_id;
    }

    public void setSample_id(String sample_id) {
        this.sample_id = sample_id;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }
}
