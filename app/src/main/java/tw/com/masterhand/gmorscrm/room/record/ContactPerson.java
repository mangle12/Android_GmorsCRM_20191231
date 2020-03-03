package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import tw.com.masterhand.gmorscrm.model.Address;
import tw.com.masterhand.gmorscrm.model.Phone;

@Entity(tableName = "ContactPerson")
public class ContactPerson extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String customer_id;// 客戶ID
    public String customer_department_name;// 客戶部門名稱
    public String first_name;// 名字
    public String last_name;// 姓氏
    public String title;// 職稱
    public String address;// 地址物件
    public String tel;// 手機
    public String email;// Email
    public String photo;// 大頭照
    public String description;// 備註
    public String wechat;// 微信


    public ContactPerson() {
        super();
        id = "";
        customer_department_name = "";
        title = "";
        address = "";
        tel = "";
        email = "";
        photo = "";
        description = "";
        wechat = "";
    }

    public String getShowName() {
        String name = "";
        if (!TextUtils.isEmpty(last_name))
            name += last_name;
        if (!TextUtils.isEmpty(first_name))
            name += first_name;
        return name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_department_name() {
        return customer_department_name;
    }

    public void setCustomer_department_name(String customer_department_name) {
        this.customer_department_name = customer_department_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Address getAddress() {
        if (TextUtils.isEmpty(address)) {
            return new Address();
        }
        return new Gson().fromJson(address, Address.class);
    }

    public void setAddress(Address address) {
        if (address != null)
            this.address = new Gson().toJson(address);
    }

    public ArrayList<Phone> getTel() {
        return new Gson().fromJson(tel, new TypeToken<ArrayList<Phone>>() {
        }.getType());
    }

    public void setTel(ArrayList<Phone> tel) {
        this.tel = new Gson().toJson(tel);
    }

    public ArrayList<String> getEmail() {
        return new Gson().fromJson(email, new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public void setEmail(ArrayList<String> email) {
        this.email = new Gson().toJson(email);
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }
}
