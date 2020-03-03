package tw.com.masterhand.gmorscrm.room.setting;

public class BaseConfig extends BaseSetting {

    public String code;
    public String name;
    public int sort;// 排序
    public String company_id;

    public BaseConfig() {
        super();
        sort = 0;
        company_id = "";
        name = "";
        code = "";
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
