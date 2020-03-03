package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.enums.ReimbursementType;

public class ReimbursementTarget {
    int type;
    String targetId;
    String name;
    float available;
    float remain;

    public ReimbursementType getType() {
        return ReimbursementType.getReimbursementTypeByValue(type);
    }

    public void setType(ReimbursementType type) {
        this.type = type.getValue();
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAvailable() {
        return available;
    }

    public void setAvailable(float available) {
        this.available = available;
    }

    public float getRemain() {
        return remain;
    }

    public void setRemain(float remain) {
        this.remain = remain;
    }
}
