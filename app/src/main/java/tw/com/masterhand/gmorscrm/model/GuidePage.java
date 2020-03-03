package tw.com.masterhand.gmorscrm.model;

public class GuidePage {
    int imgResId;
    String title, msg;

    public GuidePage(String title, String msg, int imgResId) {
        this.title = title;
        this.msg = msg;
        this.imgResId = imgResId;
    }

    public int getImgResId() {
        return imgResId;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }
}
