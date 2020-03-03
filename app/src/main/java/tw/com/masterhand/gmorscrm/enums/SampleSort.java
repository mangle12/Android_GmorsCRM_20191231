package tw.com.masterhand.gmorscrm.enums;

// 樣品排序方式
public enum SampleSort {
    ALL(0),
    DATE(1),
    COMPANY(2);

    private int code;

    SampleSort(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
