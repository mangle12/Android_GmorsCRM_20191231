package tw.com.masterhand.gmorscrm.model;

import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Task;

public class TaskWithConfig extends BaseTrip {
    Task task;
    List<File> files;

    public TaskWithConfig() {
        super();
        trip.setType(TripType.TASK);
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

}
