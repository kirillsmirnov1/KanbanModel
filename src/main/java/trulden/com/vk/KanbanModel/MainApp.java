package trulden.com.vk.KanbanModel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import trulden.com.vk.KanbanModel.model.*;
import trulden.com.vk.KanbanModel.util.Scenario;
import trulden.com.vk.KanbanModel.view.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.json.*;
import com.google.gson.Gson;
import trulden.com.vk.KanbanModel.view.ScenarioComparisonController;

public class MainApp extends Application{

    private int sceneW, sceneH;
    private boolean showBoard;

    private ArrayList<Scenario> scenarios;
    private Iterator<Scenario> scenarioIterator;

    private ArrayList<ResultOfModel> resultsOfModel;

    private Model    model;
    private Thread   modelThread;

    private Worker[] workers;
    private Task[]   tasks;

    private KanbanBoardController kanbanBoardController;
    private ScenarioComparisonController scenarioComparisonController;
    private CFDController cfdController;
    private SettingsController settingsController;

    private Stage kanbanBoardStage;
    private Stage scenarioComparisonStage;
    private Stage cfdStage;
    private Stage settingsStage;

    private Path scenariosPath = Paths.get("scenarios.json");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.settingsStage = primaryStage;
        resultsOfModel = new ArrayList<>();

        parseInitJson();

        loadSettingsWindow();
        loadKanbanBoardWindow();
        loadCFDWindow();
        loadScenariosWindow();
    }

    public void startModel() { // TODO рестарт
        readScenarioJson();
        generateWorkers();
        generateTasks();

        if(showBoard)
            kanbanBoardStage.show();
        else
            Model.setTimeToSleep(0);

        startNextScenario(scenarioIterator.next());
    }

    private void loadSettingsWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/trulden/com/vk/KanbanModel/view/Settings.fxml"));

        try {
            settingsStage = new Stage();
            settingsStage.setTitle("Kanban Model Settings");
            settingsStage.setScene(new Scene(loader.load()));

            settingsController = loader.getController();

            settingsStage.show();

            settingsStage.setOnCloseRequest(event -> System.exit(0));

            settingsController.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCFDWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/trulden/com/vk/KanbanModel/view/CFD.fxml"));

        try {
            cfdStage = new Stage();
            cfdStage.setTitle("CFD");
            cfdStage.initOwner(settingsStage);
            cfdStage.setScene(new Scene(loader.load()));

            cfdController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadScenariosWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/trulden/com/vk/KanbanModel/view/ScenarioComparison.fxml"));

        try {
            scenarioComparisonStage = new Stage();
            scenarioComparisonStage.setTitle("Scenarios result");
            scenarioComparisonStage.initOwner(settingsStage);
            scenarioComparisonStage.setScene(new Scene(loader.load()));
            scenarioComparisonStage.setMinHeight(200);
            // Set the persons into the controller.
            scenarioComparisonController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadKanbanBoardWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/trulden/com/vk/KanbanModel/view/KanbanBoard.fxml"));
        kanbanBoardStage = new Stage();
        kanbanBoardStage.setTitle("Kanban Model");
        kanbanBoardStage.setScene(new Scene(loader.load(), sceneW, sceneH));
        kanbanBoardStage.setResizable(false);

        kanbanBoardStage.initOwner(settingsStage);

        kanbanBoardController = loader.getController();
    }

    private void readScenarioJson() {
        scenarios = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(new String(Files.readAllBytes(scenariosPath)));
            JSONArray  arr = new JSONArray(obj.get("scenarios").toString());
            for(int i=0; i < arr.length(); ++i){
                Scenario sc = new Scenario();

                sc.setLinearTasksMovement(arr.getJSONObject(i).getBoolean("linearTasksMovement"));
                sc.setDefaultWIP(new Gson().fromJson(arr.getJSONObject(i).getString("defaultWIP"), int[].class));
                sc.setDeploymentFrequency(arr.getJSONObject(i).getInt("deploymentFrequency"));
                sc.setMaxWorkerEnergy(arr.getJSONObject(i).getInt("maxWorkerEnergy"));
                sc.setTaskChangePenalty(arr.getJSONObject(i).getDouble("taskChangePenalty"));

                scenarios.add(sc);
            }

            scenarioIterator = scenarios.iterator();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopModel(){
        if(model != null){
            model.timeToStop();
            modelThread.stop(); // TODO это не хорошо, переделай
        }
    }

    public void startNextScenario(Scenario scenario){

        cfdController.clear();

        model = new Model(this,
                kanbanBoardController,
                          cfdController,
                          scenario,
                          workers,
                          Arrays.stream(tasks).map(Task::new).toArray(Task[]::new)); // Эта херобора нужна чтобы карточки были неюзанные

        if(showBoard)
            kanbanBoardController.setModelAndMainApp(model, this);

        modelThread = new Thread(model);
        modelThread.start();

        model.currentModelFinishedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
                if(scenarioIterator.hasNext()){
                    if(showBoard)
                        Platform.runLater(() -> kanbanBoardController.clearEverything());
                    startNextScenario(scenarioIterator.next());
                }
        });
    }

    private void generateTasks() {
        int numberOfTasks = 50 * Model.getNumberOfDays() / Model.getNumberOfWorkers(); // TODO учитывать WIP лимиты ?
        tasks = new Task[numberOfTasks];
        for(int i = 0; i < numberOfTasks; ++i){
            tasks[i] = Task.generateRandomTask();
        }
    }

    private void generateWorkers(){
        String[] workerNames = readWorkerNames();
        workers = new Worker[Model.getNumberOfWorkers()];
        for(int i=0; i < workers.length; ++i){
            workers[i] = Worker.generateRandomWorker(workerNames[i]);
        }
    }

    private void parseInitJson() {
        try {
            JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("init.json"))));
            Model.setNumberOfDays(obj.getInt("NUMBER_OF_DAYS"));
            Model.setNumberOfWorkers(obj.getInt("NUMBER_OF_WORKERS"));
            Model.setTimeToSleep(obj.getInt("TIME_TO_SLEEP"));
            scenariosPath = Paths.get(obj.getString("scenariosPath"));
            showBoard = obj.getBoolean("showBoard");
            sceneW = obj.getInt("sceneW");
            sceneH = obj.getInt("sceneH");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] readWorkerNames(){
        int[] lineNumbers;
        int numberOfLines;
        int lineCounter = 1;

        String[] workerNames = new String[Model.getNumberOfWorkers()];

        try {
            BufferedReader br = new BufferedReader(new FileReader("shortAnimals.txt"));
            String line = br.readLine();
            numberOfLines = Integer.parseInt(line);
            lineNumbers = new Random().ints(1, numberOfLines).limit(Model.getNumberOfWorkers()).sorted().toArray();

            for (int i = 0; i < Model.getNumberOfWorkers(); ++i) {
                while (lineCounter < lineNumbers[i] - 1) {
                    br.readLine();
                    lineCounter++;
                }
                workerNames[i] = br.readLine();
                lineCounter++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return workerNames;
    }

    public void addModelResult(ResultOfModel result){
        scenarioComparisonController.addResult(resultsOfModel.size(), result);
        resultsOfModel.add(result);
    }

    public void showCFD() {
        cfdStage.show();
    }

    public void showScenariosResults() {
        scenarioComparisonStage.show();
    }

    public boolean getShowBoard(){
        return showBoard;
    }

    public void setShowBoard(boolean showBoard) {
        this.showBoard = showBoard;
    }

    public String getScenariosPathAsString() {
        return scenariosPath.toString();
    }

    public void setScenariosPath(String scenariosPath) {
        this.scenariosPath = Paths.get(scenariosPath);
    }
}
