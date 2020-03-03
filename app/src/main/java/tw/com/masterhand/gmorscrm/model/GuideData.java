package tw.com.masterhand.gmorscrm.model;

import java.util.List;

public class GuideData {
    String title;
    List<GuidePage> pageList;

    public GuideData(String title, List<GuidePage> data) {
        this.title = title;
        pageList = data;
    }

    public String getTitle() {
        return title;
    }

    public List<GuidePage> getPageList() {
        return pageList;
    }

}
