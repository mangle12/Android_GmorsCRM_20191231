package tw.com.masterhand.gmorscrm.enums;

public enum SyncType {
    SETTING(1),
    RECORD(2);

    private int code;

    SyncType(int code) {
        this.code = code;
    }

    public String getCode() {
        return String.valueOf(code);
    }

}
