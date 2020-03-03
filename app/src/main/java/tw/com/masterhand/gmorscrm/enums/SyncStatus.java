package tw.com.masterhand.gmorscrm.enums;

public enum SyncStatus {
    NONE(0),
    SUCCESS(1),
    FAIL(2);

    private int code;

    SyncStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SyncStatus getStatusByCode(int code) {
        for (SyncStatus status : SyncStatus.values()) {
            if (code == status.getCode())
                return status;
        }
        return FAIL;
    }
}
