package tw.com.masterhand.gmorscrm.enums;

public enum SampleFileType {
    FILE(1),
    PHOTO(2);

    int value;

    SampleFileType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SampleFileType getFileTypeByValue(int value) {
        for (SampleFileType type : SampleFileType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
