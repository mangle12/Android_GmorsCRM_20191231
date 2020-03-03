package tw.com.masterhand.gmorscrm.enums;

// 客戶排序方式
public enum CustomerSort {
    IMPORTANT(0),
    AREA(1),
    WORD(2);

    private int code;

    CustomerSort(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
