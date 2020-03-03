package tw.com.masterhand.gmorscrm.enums;

public enum FileType {
    FILE(1),
    PHOTO(2);

    int value;

    FileType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FileType getFileTypeByValue(int value) {
        for (FileType type : FileType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
