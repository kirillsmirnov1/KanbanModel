package trulden.com.vk.KanbanModel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import trulden.com.vk.KanbanModel.model.Model;
import trulden.com.vk.KanbanModel.model.ResultOfModel;
import trulden.com.vk.KanbanModel.model.Task;
import trulden.com.vk.KanbanModel.model.Worker;
import trulden.com.vk.KanbanModel.util.Scenario;
import trulden.com.vk.KanbanModel.view.CFDController;
import trulden.com.vk.KanbanModel.view.MainWindowController;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
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

    private ArrayList<Scenario> scenarios;
    private Iterator<Scenario> scenarioIterator;

    private ArrayList<ResultOfModel> resultsOfModel;

    private Model    model;
    private Thread   modelThread;

    private Worker[] workers;
    private Task[]   tasks;

    private MainWindowController mainWindowController;
    private ScenarioComparisonController scenarioComparisonController;
    private Stage primaryStage;
    private Stage scenarioComparisonStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        resultsOfModel = new ArrayList<>();

        parseInitJson();
        readScenarioJson();
        generateWorkers();
        generateTasks();

        loadMainWindow();
        loadScenariosWindow();

        startModel(scenarioIterator.next());
    }

    private void loadScenariosWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/trulden/com/vk/KanbanModel/view/ScenarioComparison.fxml"));

        try {
            scenarioComparisonStage = new Stage();
            scenarioComparisonStage.setTitle("Scenarios result");
            scenarioComparisonStage.initOwner(primaryStage);
            scenarioComparisonStage.setScene(new Scene(loader.load()));
            // Set the persons into the controller.
            scenarioComparisonController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/trulden/com/vk/KanbanModel/view/MainWindow.fxml"));
        primaryStage.setTitle("Kanban Model");
        primaryStage.setScene(new Scene(loader.load(), sceneW, sceneH));
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest( event -> System.exit(0));

        mainWindowController = loader.getController();

        primaryStage.show();
    }

    private void readScenarioJson() {
        scenarios = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("scenarios.json"))));
            JSONArray  arr = new JSONArray(obj.get("scenarios").toString());
            for(int i=0; i < arr.length(); ++i){
                Scenario sc = new Scenario();

                sc.setLinearTasksMovement(arr.getJSONObject(i).getBoolean("linearTasksMovement"));
                sc.setDefaultWIP(new Gson().fromJson(arr.getJSONObject(i).getString("defaultWIP"), int[].class));
                sc.setDeploymentFrequency(arr.getJSONObject(i).getInt("deploymentFrequency"));

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

    public void startModel(Scenario scenario){
        model = new Model(this,
                          mainWindowController,
                          scenario,
                          workers,
                          Arrays.stream(tasks).map(Task::new).toArray(Task[]::new)); // Эта херобора нужна чтобы карточки были неюзанные

        mainWindowController.setModelAndMainApp(model, this);
        modelThread = new Thread(model);
        modelThread.start();

        model.currentModelFinishedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
                if(scenarioIterator.hasNext()){
                    Platform.runLater(() -> mainWindowController.clearEverything());
                    startModel(scenarioIterator.next()); 
                }
        });
    }

    private void generateTasks() {
        tasks = new Task[20 * Model.getNumberOfDays() / Model.getNumberOfWorkers()];
        for(int i = 0; i < tasks.length; ++i){
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

    public void showCFD() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/trulden/com/vk/KanbanModel/view/CFD.fxml"));

        try {
            Stage CFDStage = new Stage();
            CFDStage.setTitle("CFD");
            CFDStage.initOwner(primaryStage);
            CFDStage.setScene(new Scene(loader.load()));

            // Set the persons into the controller.
            CFDController controller = loader.getController();

            controller.setDayTracking(model.currentDayProperty(), model.getCFD());

            CFDStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addModelResult(ResultOfModel result){
        scenarioComparisonController.addResult(resultsOfModel.size(), result);
        resultsOfModel.add(result);
    }

    public void showScenariosResults() {
        scenarioComparisonStage.show();
    }
}
