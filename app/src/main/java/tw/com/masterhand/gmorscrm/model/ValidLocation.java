package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.enums.TripType;

public class ValidLocation {
    public String id;// trip id

    public int type;// 種類 1:visit 2:office
    public String longitude;//經度
    public String latitude;//緯度

    public void setId(String id) {
        this.id = id;
    }

    public void setType(TripType type) {
        this.type = type.getValue();
    }

    public void setLongitude(double longitude) {
        this.longitude = String.valueOf(longitude);
    }

    public void setLatitude(double latitude) {
        this.latitude = String.valueOf(latitude);
    }
}
