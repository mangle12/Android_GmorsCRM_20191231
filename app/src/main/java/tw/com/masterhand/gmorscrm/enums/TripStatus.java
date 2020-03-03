package tw.com.masterhand.gmorscrm.enums;

public enum TripStatus {
    NORMAL(1),
    DELAY(2),
    CANCEL(3);

    int value;

    TripStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TripStatus getTripTypeByValue(int value) {
        for (TripStatus tripType : TripStatus.values()) {
            if (tripType.getValue() == value) {
                return tripType;
            }
        }
        return null;
    }
}
