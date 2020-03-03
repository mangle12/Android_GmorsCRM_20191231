package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.ReimbursementItem;
import tw.com.masterhand.gmorscrm.room.setting.ConfigReimbursementItem;

public class ReimbursementItemWithConfig {
    public ReimbursementItem item;
    public ConfigReimbursementItem config;
    public List<String> files;

    public ReimbursementItemWithConfig() {
        item = new ReimbursementItem();
        files = new ArrayList<>();
    }

    public ReimbursementItem getItem() {
        return item;
    }

    public void setItem(ReimbursementItem item) {
        this.item = item;
    }

    public ConfigReimbursementItem getConfig() {
        return config;
    }

    public void setConfig(ConfigReimbursementItem config) {
        this.config = config;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
