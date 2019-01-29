package trulden.com.vk.KanbanModel.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import trulden.com.vk.KanbanModel.MainApp;
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

    private MainApp mainApp;

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

    public void watchTask(Task task){
        Label label = new Label(task.toString());
        label.setWrapText(true);
        label.setMinHeight(40);

        Platform.runLater(() -> {
            if(!stagesUpVBoxHashMap.get(BACKLOG).getChildren().contains(label))
                stagesUpVBoxHashMap.get(BACKLOG).getChildren().add(label);
        });

        // Обновление величины выполнения таски
        task.totalAdvanceProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() ->
            label.setText(task.toString())
        ));

        // Изменение готовности таски в столбце
        task.doneAtCurrentStageProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {

            StageType currentStage = task.getStage();

            // Если таска готова
            if(newValue) {
                if(task.stageProperty().get() == DEPLOYMENT)
                    stagesUpVBoxHashMap.get(DEPLOYMENT).getChildren().remove(label);
                else{
                    stagesUpVBoxHashMap.get(currentStage).getChildren().remove(label);

                    if(!stagesDownVBoxHashMap.get(currentStage).getChildren().contains(label))
                        stagesDownVBoxHashMap.get(currentStage).getChildren().add(label);
                }

                updateWIPLimit(task.stageProperty().get());
            }
            // С другим случаем должна справляться следующая функция
        }));

        // Изменение StageType таски
        task.stageProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
            ObservableList<Node> labelList;

            labelList = (oldValue == BACKLOG) ? stagesUpVBoxHashMap.get(oldValue).getChildren() : stagesDownVBoxHashMap.get(oldValue).getChildren();
            labelList.remove(label);

            if(newValue == DEPLOYMENT)
                label.setText(task.toString());

            if(!task.doneAtCurrentStageProperty().get()) {               // Тут может быть дедлок (((
                if(!stagesUpVBoxHashMap.get(newValue).getChildren().contains(label))
                    stagesUpVBoxHashMap.get(newValue).getChildren().add(label);
            }
            else {
                if(!stagesDownVBoxHashMap.get(newValue).getChildren().contains(label))
                    stagesDownVBoxHashMap.get(newValue).getChildren().add(label);
            }

            updateWIPLimit(oldValue);
            updateWIPLimit(newValue);
        }));
    }

    public void setModelAndMainApp(Model model, MainApp mainApp) {

        this.mainApp = mainApp;

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

    @FXML
    private void handleShowCFD(){
        mainApp.showCFD();
    }

    @FXML
    private void handleRestart() {
        mainApp.stopModel();
        clearEverything();
        mainApp.startModel();
    }

    private void clearEverything(){
        for(StageType stage : StageType.values()){
            stagesUpVBoxHashMap.get(stage).getChildren().clear();
            if(stage != BACKLOG && stage != DEPLOYMENT)
                stagesDownVBoxHashMap.get(stage).getChildren().clear();
        }
        workersGrid.getChildren().clear();
    }
}
