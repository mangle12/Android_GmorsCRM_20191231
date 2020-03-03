package tw.com.masterhand.gmorscrm.model;

import java.util.Date;

public class Notify {
    String id;// 通知/公告id
    String title;// 標題
    String from;// 發送通知的使用者或部門名稱
    Date updated_at;// 更新時間

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFrom() {
        return from;
    }

    public Date getUpdated_at() {
        return updated_at;
    }
}
