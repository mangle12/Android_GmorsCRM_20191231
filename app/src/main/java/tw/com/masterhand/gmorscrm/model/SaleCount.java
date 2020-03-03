package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

public class SaleCount {
    Stage stage_1;
    Stage stage_2;
    Stage stage_3;
    Stage stage_4;
    Stage stage_5;
    Stage stage_6;

    public List<Stage> getAllStage() {
        List<Stage> allList = new ArrayList<>();
        allList.add(stage_1);
        allList.add(stage_2);
        allList.add(stage_3);
        allList.add(stage_4);
        allList.add(stage_5);
        allList.add(stage_6);
        return allList;
    }
}
