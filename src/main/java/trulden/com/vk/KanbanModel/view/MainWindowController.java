package trulden.com.vk.KanbanModel.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import trulden.com.vk.KanbanModel.model.Model;
import trulden.com.vk.KanbanModel.model.StageType;
import trulden.com.vk.KanbanModel.model.Task;
import trulden.com.vk.KanbanModel.model.Worker;

import java.util.HashMap;

import static trulden.com.vk.KanbanModel.model.StageType.*;

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
    private VBox analysisTodoVBox;
    @FXML
    private VBox analysisDoneVBox;
    @FXML
    private VBox designTodoVBox;
    @FXML
    private VBox designDoneVBox;
    @FXML
    private VBox implementationTodoVBox;
    @FXML
    private VBox implementationDoneVBox;
    @FXML
    private VBox integrationTodoVBox;
    @FXML
    private VBox integrationDoneVBox;
    @FXML
    private VBox documentationTodoVBox;
    @FXML
    private VBox documentationDoneVBox;
    @FXML
    private VBox testingTodoVBox;
    @FXML
    private VBox testingDoneVBox;
    @FXML
    private VBox deploymentVBox;

    // Label'ы в статус-баре
    @FXML
    private Label dayLabel;
    @FXML
    private Label productivityLabel;
    @FXML
    private Label tasksDeployedLabel;

    @FXML
    private GridPane workersGrid;
    private int workersGridX;

    private HashMap<StageType, VBox> stagesUpVBoxHashMap;
    private HashMap<StageType, VBox> stagesDownVBoxHashMap;
    private HashMap<StageType, Label> stagesLabelHashMap;

    private Model model;

    @FXML
    private void initialize(){
        stagesUpVBoxHashMap = new HashMap<>();
        stagesUpVBoxHashMap.put(BACKLOG, backlogVBox);
        stagesUpVBoxHashMap.put(ANALYSIS, analysisTodoVBox);
        stagesUpVBoxHashMap.put(DESIGN, designTodoVBox);
        stagesUpVBoxHashMap.put(IMPLEMENTATION, implementationTodoVBox);
        stagesUpVBoxHashMap.put(INTEGRATION, integrationTodoVBox);
        stagesUpVBoxHashMap.put(DOCUMENTATION, documentationTodoVBox);
        stagesUpVBoxHashMap.put(TESTING, testingTodoVBox);
        stagesUpVBoxHashMap.put(DEPLOYMENT, deploymentVBox);

        stagesDownVBoxHashMap = new HashMap<>();
        stagesDownVBoxHashMap.put(ANALYSIS, analysisDoneVBox);
        stagesDownVBoxHashMap.put(DESIGN, designDoneVBox);
        stagesDownVBoxHashMap.put(IMPLEMENTATION, implementationDoneVBox);
        stagesDownVBoxHashMap.put(INTEGRATION, integrationDoneVBox);
        stagesDownVBoxHashMap.put(DOCUMENTATION, documentationDoneVBox);
        stagesDownVBoxHashMap.put(TESTING, testingDoneVBox);

        stagesLabelHashMap = new HashMap<>();
        stagesLabelHashMap.put(BACKLOG, backlogLabel);
        stagesLabelHashMap.put(ANALYSIS, analysisLabel);
        stagesLabelHashMap.put(DESIGN, designLabel);
        stagesLabelHashMap.put(IMPLEMENTATION, implementationLabel);
        stagesLabelHashMap.put(INTEGRATION, integrationLabel);
        stagesLabelHashMap.put(DOCUMENTATION, documentationLabel);
        stagesLabelHashMap.put(TESTING, testingLabel);
        stagesLabelHashMap.put(DEPLOYMENT, deploymentLabel);

        workersGridX = workersGrid.getColumnCount();
    }

    private void updateWIPLimit(StageType stage){
            Platform.runLater(() ->
                    stagesLabelHashMap.get(stage)
                            .setText(
                                    stage.toString()
                                    + " [" + stagesUpVBoxHashMap.get(stage).getChildren().size()
                                    + (stage == DEPLOYMENT
                                            ? "]" // Если деплоймент, то скобочки хватит
                                            : "/" + Model.DEFAULT_WIP[stage.ordinal()] + "]" // Иначе – нужно лимит писать
                                      )
                            ));
    }

    public void addTask(Task task, StageType stage, boolean done) {
        Label label = new Label(task.toString());
        label.setWrapText(true);
        label.setMinHeight(40);

        if(stage == BACKLOG || stage == DEPLOYMENT || !done)
            Platform.runLater(() -> stagesUpVBoxHashMap.get(stage).getChildren().add(label) ); // лямбды это магия
        else
            Platform.runLater(() -> stagesDownVBoxHashMap.get(stage).getChildren().add(label) );

        updateWIPLimit(stage);
    }

    public void updateTask(Task task, StageType stage){
        ObservableList<Node> labelList = stagesUpVBoxHashMap.get(stage).getChildren();

        for(int i=0; i < labelList.size(); i++){
            if(((Label)labelList.get(i)).getText().contains(task.getName())){
                int makingIteratorConstant = i;
                Platform.runLater(() -> ((Label) labelList.get(makingIteratorConstant)).setText(task.toString()) );
                break;
            }
        }
    }

    public void removeTask(Task task, StageType stage, boolean done){
        ObservableList<Node> labelList;

        if(stage == BACKLOG || stage == DEPLOYMENT || !done)
            labelList = stagesUpVBoxHashMap.get(stage).getChildren();
        else
            labelList = stagesDownVBoxHashMap.get(stage).getChildren();

        for(int i=0; i < labelList.size(); i++){
            if(((Label)labelList.get(i)).getText().contains(task.getName())){
                int makingIteratorConstant = i;
                Platform.runLater(() -> labelList.remove(makingIteratorConstant) );

                updateWIPLimit(stage);
                break;
            }
        }
    }

    public void moveTaskToFinished(Task task, StageType stage){
        removeTask(task, stage, false);
        addTask(task, stage, true);
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

        for(Worker worker : model.getWorkers()){
            Label label = new Label(worker.toString());
            label.setWrapText(true);
            label.setMinHeight(60);
            label.setMinWidth(150);
            workersGrid.add(label, worker.getID() % workersGridX, worker.getID() / workersGridX);

            worker.energyProperty().addListener(
                    ((observable, oldValue, newValue) ->
                    Platform.runLater(() ->
                        label.setText(worker.toString()))));
        }

    }
}
