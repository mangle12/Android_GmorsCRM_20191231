package tw.com.masterhand.gmorscrm.model;

public class MultipleSubmit {
    public String trip_ids;// 複數提交的TripID的內容，字串，每個TripID以逗號(,)分開。

    public String getTrip_ids() {
        return trip_ids;
    }

    public void setTrip_ids(String trip_ids) {
        this.trip_ids = trip_ids;
    }
}
