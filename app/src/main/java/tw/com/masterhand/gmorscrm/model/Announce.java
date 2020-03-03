package tw.com.masterhand.gmorscrm.model;

import java.util.Date;

public class Announce {
    String id;// 通知/公告id
    String title;// 標題
    Image default_image;// 封面url
    String from;// 發送通知的使用者或部門名稱
    Date updated_at;// 更新時間

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return default_image == null ? "" : default_image.getFile_url();
    }

    public String getFrom() {
        return from;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    class Image {
        String id;
        String announce_id;
        int sort;
        String file_url;

        public String getFile_url() {
            return file_url;
        }
    }
}
