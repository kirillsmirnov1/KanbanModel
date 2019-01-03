package trulden.com.vk.KanbanModel.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import trulden.com.vk.KanbanModel.model.StageType;
import trulden.com.vk.KanbanModel.model.Task;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainWindowController {

    @FXML
    private VBox backlogVBox;
    @FXML
    private VBox analysisVBox;
    @FXML
    private VBox designVBox;
    @FXML
    private VBox implementationVBox;
    @FXML
    private VBox integrationVBox;
    @FXML
    private VBox documentationVBox;
    @FXML
    private VBox testingVBox;
    @FXML
    private VBox deploymentVBox;

    private HashMap<StageType, VBox> stagesHashMap;

    @FXML
    private void initialize(){
        stagesHashMap = new HashMap<>();
        stagesHashMap.put(StageType.BACKLOG, backlogVBox);
        stagesHashMap.put(StageType.ANALYSIS, analysisVBox);
        stagesHashMap.put(StageType.DESIGN, designVBox);
        stagesHashMap.put(StageType.IMPLEMENTATION, implementationVBox);
        stagesHashMap.put(StageType.INTEGRATION, integrationVBox);
        stagesHashMap.put(StageType.DOCUMENTATION, documentationVBox);
        stagesHashMap.put(StageType.TESTING, testingVBox);
        stagesHashMap.put(StageType.DEPLOYMENT, deploymentVBox);
    }

    public void addTask(Task task, StageType stage) {

        // TODO выгрузить вьюшку в отдельный поток, иначе этот слип все вешает
//        try{
//            TimeUnit.SECONDS.sleep(1);
//            // this.wait(1000);
//        } catch (InterruptedException e){}
        String str = task.toString();
        Label label = new Label(str);
        label.setWrapText(true);
        label.setMinHeight(40);
        stagesHashMap.get(stage).getChildren().add(label);
    }

    public void removeTask(Task task, StageType stage){
        ObservableList<Node> labelList = stagesHashMap.get(stage).getChildren();

        for(int i=0; i < labelList.size(); i++){
            if(((Label)labelList.get(i)).getText().contains(task.getName())){
                labelList.remove(i);
                break;
            }
        }
    }
}
