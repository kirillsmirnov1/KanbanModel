package trulden.com.vk.KanbanModel;

import javafx.application.Application;
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

// Класс главного приложения
// Грузит формы, читает инишни, сценарии, генерит сотрудников и задачи
// Запускает моделирование с указанными параметрами
public class MainApp extends Application{

    // Ширина и высота окна канбан-доски
    private int kanbanBoardW, kanbanBoardH;
    // Количество завершённых сценариев
    private int scenariosFinished = 0;
    // Отображать канбан-доску?
    private boolean showingKanbanBoard;

    // Итератор сценариев
    private Iterator<Scenario> scenarioIterator;

    // Текущая модель
    private Model    model;
    // Поток модели
    private Thread   modelThread;

    // Соотрудники
    private Worker[] workers;
    // Задачи
    private Task[]   tasks;

    // Контроллеры форм
    private KanbanBoardController kanbanBoardController;
    private ScenarioComparisonController scenarioComparisonController;
    private CFDController cfdController;
    private SettingsController settingsController;

    // Стэйджы форм
    private Stage kanbanBoardStage;
    private Stage scenarioComparisonStage;
    private Stage cfdStage;
    private Stage settingsStage;

    // Путь к сценариям
    private Path scenariosPath;

    public static void main(String[] args) {
        launch(args);
    }

    // Показывает стартовое окно настроек
    @Override
    public void start(Stage primaryStage){
        this.settingsStage = primaryStage;

        readInitJson();

        loadSettingsWindow();

        settingsStage.show();
    }

    // Первый запуск модели − читает сценарии, генерит сотрудников и таски
    public void startModel() { // TODO рестарт
        readScenarioJson();

        generateWorkers();
        generateTasks();

        loadScenariosResultsWindow();

        if(showingKanbanBoard){
            loadKanbanBoardWindow();
            loadCFDWindow();
            kanbanBoardStage.show();
        }
        else
            Model.setTimeToSleep(0);

        startScenario(scenarioIterator.next());
    }

    // Команда на прекращение работы модели
    public void stopModel(){
        if(model != null){
            model.timeToStop();
            modelThread.stop(); // TODO это не хорошо, переделай
        }
    }

    // Запуск сценария в модели
    public void startScenario(Scenario scenario){
    // TODO множественный прогон − просто цикл внутри этой функции
    // TODO с сохранением промежуточных результатов и их усреднением

        // Инициирую новую модель
        model = new Model(this,
                kanbanBoardController,
                          cfdController,
                          scenario,
                          workers,
                          Arrays.stream(tasks).map(Task::new).toArray(Task[]::new)); // Эта херобора нужна чтобы карточки были неюзанные


        if(showingKanbanBoard) {
            // Чищу CFD от прошлых прогонов
            cfdController.clear();
            kanbanBoardController.clearEverything();
            // Даю доске ссылки на модель и главное приложение
            // FIXME не круто, что вьюха получает доступ к классам модели, надо бы переделать
            kanbanBoardController.setModelAndMainApp(model, this);
        }

        // Запуск потока модели
        modelThread = new Thread(model);
        modelThread.start();

        // Как завершилась одна модель − запускаем следующую
        model.currentModelFinishedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue)
                if(scenarioIterator.hasNext()){
                    startScenario(scenarioIterator.next());
                }
        });
    }

    // Генерация задач
    // Задачи генерятся один раз, используются во всех моделях и сценариях
    // FIXME если я хочу разных результатов, задачи должны генерироваться для каждого прогона
    private void generateTasks() {
        int numberOfTasks = 50 * Model.getNumberOfDays() / Model.getNumberOfWorkers(); // FIXME учитывать WIP лимиты ?
        tasks = new Task[numberOfTasks];
        for(int i = 0; i < numberOfTasks; ++i){
            tasks[i] = Task.generateRandomTask();
        }
    }

    // Генерация сотрудников
    // FIXME обеспечивать заполненность стека способностей
    // TODO читать сотрудников из файла
    private void generateWorkers(){
        String[] workerNames = readWorkerNames();
        workers = new Worker[Model.getNumberOfWorkers()];
        for(int i=0; i < workers.length; ++i){
            workers[i] = Worker.generateRandomWorker(workerNames[i]);
        }
    }

    // Чтение из текстового файла (случайных) имен сотрудников
    private static String[] readWorkerNames(){
        int[] lineNumbers;
        int numberOfLines;
        int lineCounter = 1;

        String[] workerNames = new String[Model.getNumberOfWorkers()];

        try {
            BufferedReader br = new BufferedReader(new FileReader("shortAnimals.txt")); // TODO путь к именам сотрудников из инишника
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

    // Чтение сценария из файла
    private void readScenarioJson() {
        ArrayList<Scenario> scenarios = new ArrayList<>();
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

            // Для работы со сценариями достаточно их итератора
            scenarioIterator = scenarios.iterator();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Чтение инишника
    private void readInitJson() {
        try {
            JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("init.json"))));
            Model.setNumberOfDays(obj.getInt("NUMBER_OF_DAYS"));
            Model.setNumberOfWorkers(obj.getInt("NUMBER_OF_WORKERS"));
            Model.setTimeToSleep(obj.getInt("TIME_TO_SLEEP"));
            scenariosPath = Paths.get(obj.getString("scenariosPath"));
            showingKanbanBoard = obj.getBoolean("showBoard");
            kanbanBoardW = obj.getInt("kanbanBoardW");
            kanbanBoardH = obj.getInt("kanbanBoardH");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Сохранение результата прогона сценария
    public void addScenarioResult(ResultOfModel result){
        scenarioComparisonController.addResult(scenariosFinished++, result);
    }

    // Загрузка стартового окна настроек
    private void loadSettingsWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/trulden/com/vk/KanbanModel/view/Settings.fxml"));

        try {
            settingsStage = new Stage();
            settingsStage.setTitle("Kanban Model Settings");
            settingsStage.setScene(new Scene(loader.load()));

            settingsController = loader.getController();

            settingsStage.setOnCloseRequest(event -> System.exit(0));

            settingsController.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка окна с CFD диаграммой
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

    // Загрузка окна с результатами сценариев
    private void loadScenariosResultsWindow() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/trulden/com/vk/KanbanModel/view/ScenarioComparison.fxml"));

        try {
            scenarioComparisonStage = new Stage();
            scenarioComparisonStage.setTitle("Scenarios result");
            scenarioComparisonStage.initOwner(settingsStage);
            scenarioComparisonStage.setScene(new Scene(loader.load()));
            scenarioComparisonStage.setMinHeight(200);

            scenarioComparisonController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка окна с канбан-доской
    private void loadKanbanBoardWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/trulden/com/vk/KanbanModel/view/KanbanBoard.fxml"));
            kanbanBoardStage = new Stage();
            kanbanBoardStage.setTitle("Kanban Model");
            kanbanBoardStage.setScene(new Scene(loader.load(), kanbanBoardW, kanbanBoardH));
            kanbanBoardStage.setResizable(false);

            kanbanBoardStage.initOwner(settingsStage);

            kanbanBoardController = loader.getController();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void showCFD() { cfdStage.show(); }

    public void showScenariosResults() { scenarioComparisonStage.show(); }

    public boolean showingKanbanBoard(){ return showingKanbanBoard; }

    public void setShowBoard(boolean showingKanbanBoard) { this.showingKanbanBoard = showingKanbanBoard; }

    public String getScenariosPathAsString() { return scenariosPath.toString(); }

    public void setScenariosPath(String scenariosPath) { this.scenariosPath = Paths.get(scenariosPath); }
}
