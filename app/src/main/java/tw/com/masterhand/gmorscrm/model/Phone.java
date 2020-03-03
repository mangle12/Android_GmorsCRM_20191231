package tw.com.masterhand.gmorscrm.model;

import android.text.TextUtils;

public class Phone {
    String type;
    String tel;
    String ext;

    public Phone() {
        type = "";
        tel = "";
        ext = "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTel() {
        if (TextUtils.isEmpty(tel)) {
            return "";
        } else {
            return tel.replace(" ", "");
        }
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getShowPhone() {
        String show = "";
        if (!TextUtils.isEmpty(getTel()))
            show += getTel();
        if (!TextUtils.isEmpty(ext))
            show += "#" + ext;
        return show;
    }
}
