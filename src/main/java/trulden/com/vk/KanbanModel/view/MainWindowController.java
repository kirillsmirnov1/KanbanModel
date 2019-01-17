package trulden.com.vk.KanbanModel.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import trulden.com.vk.KanbanModel.model.Model;
import trulden.com.vk.KanbanModel.model.StageType;
import trulden.com.vk.KanbanModel.model.Task;

import java.util.HashMap;

public class MainWindowController {

    // Label'ы с названиями стадий и WIP лимитом
    @FXML
    private Label backlogLabel;
    @FXML
    private Label analysisLabel;
    @FXML
    private Label designLabel;
    @FXML
    private Label implementationLabel;
    @FXML
    private Label integrationLabel;
    @FXML
    private Label documentationLabel;
    @FXML
    private Label testingLabel;
    @FXML
    private Label deploymentLabel;

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

    // Label'ы в статус-баре
    @FXML
    private Label dayLabel;
    @FXML
    private Label productivityLabel;
    @FXML
    private Label tasksDeployedLabel;

    private HashMap<StageType, VBox> stagesVBoxHashMap;
    private HashMap<StageType, Label> stagesLabelHashMap;

    private Model model;

    @FXML
    private void initialize(){
        stagesVBoxHashMap = new HashMap<>();
        stagesVBoxHashMap.put(StageType.BACKLOG, backlogVBox);
        stagesVBoxHashMap.put(StageType.ANALYSIS, analysisVBox);
        stagesVBoxHashMap.put(StageType.DESIGN, designVBox);
        stagesVBoxHashMap.put(StageType.IMPLEMENTATION, implementationVBox);
        stagesVBoxHashMap.put(StageType.INTEGRATION, integrationVBox);
        stagesVBoxHashMap.put(StageType.DOCUMENTATION, documentationVBox);
        stagesVBoxHashMap.put(StageType.TESTING, testingVBox);
        stagesVBoxHashMap.put(StageType.DEPLOYMENT, deploymentVBox);

        stagesLabelHashMap = new HashMap<>();
        stagesLabelHashMap.put(StageType.BACKLOG, backlogLabel);
        stagesLabelHashMap.put(StageType.ANALYSIS, analysisLabel);
        stagesLabelHashMap.put(StageType.DESIGN, designLabel);
        stagesLabelHashMap.put(StageType.IMPLEMENTATION, implementationLabel);
        stagesLabelHashMap.put(StageType.INTEGRATION, integrationLabel);
        stagesLabelHashMap.put(StageType.DOCUMENTATION, documentationLabel);
        stagesLabelHashMap.put(StageType.TESTING, testingLabel);
        stagesLabelHashMap.put(StageType.DEPLOYMENT, deploymentLabel);
    }

    public void addTask(String taskText, StageType stage) {
        Label label = new Label(taskText);
        label.setWrapText(true);
        label.setMinHeight(40);
        Platform.runLater(() -> stagesVBoxHashMap.get(stage).getChildren().add(label) ); // лямбды это магия
        if(stage != StageType.DEPLOYMENT)
            Platform.runLater(() -> stagesLabelHashMap.get(stage).setText(stage.toString() + " [" + stagesVBoxHashMap.get(stage).getChildren().size() + "/" + Model.DEFAULT_WIP[stage.ordinal()] + "]"));
        else
            Platform.runLater(() -> stagesLabelHashMap.get(stage).setText(stage.toString() + " [" + stagesVBoxHashMap.get(stage).getChildren().size() + "]"));
    }

    public void updateTask(Task task, StageType stage){
        ObservableList<Node> labelList = stagesVBoxHashMap.get(stage).getChildren();

        for(int i=0; i < labelList.size(); i++){
            if(((Label)labelList.get(i)).getText().contains(task.getName())){
                int makingIteratorConstant = i;
                Platform.runLater(() -> ((Label) labelList.get(makingIteratorConstant)).setText(task.toString()) );
                break;
            }
        }
    }

    public void removeTask(Task task, StageType stage){
        ObservableList<Node> labelList = stagesVBoxHashMap.get(stage).getChildren();

        for(int i=0; i < labelList.size(); i++){
            if(((Label)labelList.get(i)).getText().contains(task.getName())){
                int makingIteratorConstant = i;
                Platform.runLater(() -> labelList.remove(makingIteratorConstant) );
                if(stage != StageType.DEPLOYMENT)
                    Platform.runLater(() -> stagesLabelHashMap.get(stage).setText(stage.toString() + " [" + stagesVBoxHashMap.get(stage).getChildren().size() + "/" + Model.DEFAULT_WIP[stage.ordinal()] + "]"));
                else
                    Platform.runLater(() -> stagesLabelHashMap.get(stage).setText(stage.toString() + " [" + stagesVBoxHashMap.get(stage).getChildren().size() + "]"));
                break;
            }
        }
    }

    public void setModel(Model model) {
        this.model = model;

        model.currentDayProperty().addListener(
                (observable, oldValue, newValue) ->
                        Platform.runLater(() ->
                                dayLabel.setText("Day: " + newValue)));

        model.productivityLevelProperty().addListener(
                (observable, oldValue, newValue) ->
                        Platform.runLater(() ->
                            productivityLabel.setText("Productivity bar: " + (int) (newValue.doubleValue() * 100)))
        );

        model.tasksDeployedProperty().addListener(
                (observable, oldValue, newValue) ->
                        Platform.runLater(() ->
                            tasksDeployedLabel.setText("Tasks deployed: " + newValue))
        );
    }
}
