package tw.com.masterhand.gmorscrm.model;

import java.util.Date;
import java.util.List;

public class NewsDetail {
    String id;// 通知/公告id
    String title;// 標題
    String message;// 內文
    List<Image> images;// 圖片URL
    int style;// 版型(預留欄位，先都回傳0)
    String from;// 發送通知的使用者或部門名稱
    Date updated_at;// 更新時間

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return message;
    }

    public List<Image> getImages() {
        return images;
    }

    public int getStyle() {
        return style;
    }

    public String getFrom() {
        return from;
    }

    public Date getUpdated_at() {
        return updated_at;
    }
}
