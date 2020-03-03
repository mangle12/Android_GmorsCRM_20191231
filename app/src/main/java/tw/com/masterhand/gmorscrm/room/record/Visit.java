package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import tw.com.masterhand.gmorscrm.enums.VisitType;
import tw.com.masterhand.gmorscrm.model.Address;

@Entity(tableName = "Visit")
public class Visit extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String trip_id;// 行事曆ID
    public String address;// 地址
    public int type;// 0:一般拜訪 1:電話拜訪

    public Visit() {
        super();
        id = "";
        address = "";
        type = VisitType.NORMAL.getValue();
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public VisitType getType() {
        return VisitType.getTypeByValue(type);
    }

    public void setType(VisitType type) {
        this.type = type.getValue();
    }
}
