package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Date;

import tw.com.masterhand.gmorscrm.model.Address;

@Entity(tableName = "Company")
public class Company extends BaseSetting {
    @NonNull
    @PrimaryKey
    public String id;

    public String name;// 公司名稱
    public String address;// 地址

    public Company() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        if (TextUtils.isEmpty(address))
            return new Address();
        return new Gson().fromJson(address, Address.class);
    }

    public void setAddress(Address address) {
        if (address != null)
            this.address = new Gson().toJson(address);
    }
}
