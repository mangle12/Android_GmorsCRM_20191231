package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.FileType;
import tw.com.masterhand.gmorscrm.enums.ParentType;

@Entity(tableName = "File")
public class File extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public int type;// int 種類 1:檔案 2:照片
    public String name;// string 名稱
    public int parent_type;
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    public String file;// string BASE64圖片

    //新增user_id
    public String user_id;

    public File() {
        super();
        id = "";
        user_id = "";
        type = FileType.PHOTO.getValue();
        name = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FileType getType() {
        return FileType.getFileTypeByValue(type);
    }

    public void setType(FileType type) {
        this.type = type.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public ParentType getParent_type() {
        return ParentType.getTypeByValue(parent_type);
    }

    public void setParent_type(ParentType parent_type) {
        this.parent_type = parent_type.getValue();
    }

    public String getFileUrl() {
        return ApiHelper.IMAGE_URL + id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
