package trulden.com.vk.KanbanModel.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import trulden.com.vk.KanbanModel.model.StageType;
import trulden.com.vk.KanbanModel.model.Task;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

// TODO отображать WIP лимиты и заполнение столбцов

public class MainWindowController {

    // Label'ы с названиями стадий и WIP лимитом

    // VBox'ы в которых хранятся таски
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

    public void addTask(String taskText, StageType stage) {
        Label label = new Label(taskText);
        label.setWrapText(true);
        label.setMinHeight(40);
        Platform.runLater(() -> stagesHashMap.get(stage).getChildren().add(label) ); // лямбды это магия
    }


    public void updateTask(Task task, StageType stage){
        ObservableList<Node> labelList = stagesHashMap.get(stage).getChildren();

        for(int i=0; i < labelList.size(); i++){
            if(((Label)labelList.get(i)).getText().contains(task.getName())){
                int makingIteratorConstant = i;
                Platform.runLater(() -> ((Label) labelList.get(makingIteratorConstant)).setText(task.toString()) );
                break;
            }
        }
    }

    public void removeTask(Task task, StageType stage){
        ObservableList<Node> labelList = stagesHashMap.get(stage).getChildren();

        for(int i=0; i < labelList.size(); i++){
            if(((Label)labelList.get(i)).getText().contains(task.getName())){
                int makingIteratorConstant = i;
                Platform.runLater(() -> labelList.remove(makingIteratorConstant) );
                break;
            }
        }
    }
}
