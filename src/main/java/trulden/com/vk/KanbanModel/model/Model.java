package trulden.com.vk.KanbanModel.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import trulden.com.vk.KanbanModel.MainApp;
import trulden.com.vk.KanbanModel.model.stage.Stage;
import trulden.com.vk.KanbanModel.model.stage.StageStorage;
import trulden.com.vk.KanbanModel.model.stage.StageType;
import trulden.com.vk.KanbanModel.model.stage.StageWorking;
import trulden.com.vk.KanbanModel.util.Scenario;
import trulden.com.vk.KanbanModel.util.Util;
import trulden.com.vk.KanbanModel.view.CFDController;
import trulden.com.vk.KanbanModel.view.KanbanBoardController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

// Модель рабочих процессов
public class Model implements Runnable{
    // Сценарий модели
    private final Scenario scenario;
    // Ссылка на главное приложение
    private final MainApp mainApp;
    // Ссылка на контроллер доски
    private KanbanBoardController kanbanBoardController;

    // Флаг завершенности модели.
    // В режиме с доской нужно ждать, пока завершится каждый прогон сценария
    private BooleanProperty currentModelFinished = new SimpleBooleanProperty(false);

    // Стадии с тасками
    private HashMap<StageType, Stage>  stages;
    // Сотрудники
    private Worker[] workers;

    // Данные для построения CFD-диаграммы
    private HashMap<Integer, int[]> CFD;

    // Время прохождения всей доски и рабочих стадий каждой из карточек
    private ArrayList<Integer> leadTime;
    private ArrayList<Integer> cycleTime;

    // Количество работников
    private static int   NUMBER_OF_WORKERS;
    // Длительность выполнения модели
    private static int   NUMBER_OF_DAYS;
    // Задержка выполнения модели для отображения изменений на канбан доске
    private static int   UI_REFRESH_DELAY;
    // Печатать промежуточные результаты модели в консоль?
    private static boolean PRINTINGS_RESULTS_TO_CONSOLE;

    // Частота деплоймента
    private int   deploymentFrequency;
    // Ограничение на количество задач в работе
    public  int[] WIPLimits;

    // Необходимый уровень навыка для взятия задач
    private DoubleProperty reqSkillLevel;
    // Текущий день модели
    private IntegerProperty currentDay;
    // Количество задеплойенных таск
    private IntegerProperty tasksDeployed;

    // Флаг экстренного завершения модели
    private BooleanProperty timeToStop = new SimpleBooleanProperty(false);

    // Конструктор модели
    public Model(MainApp mainApp, KanbanBoardController kanbanBoardController, CFDController cfdController, Scenario scenario, Worker[] workers) {
        this.mainApp = mainApp;
        this.kanbanBoardController = kanbanBoardController;
        this.workers = workers;
        this.scenario = scenario;

        WIPLimits = scenario.getWIPLimits();
        deploymentFrequency = scenario.getDeploymentFrequency();
        Worker.setMaxEnergy(scenario.getMaxWorkerEnergy());
        Worker.setTaskChangePenalty(scenario.getTaskChangePenalty());

        stages  = new HashMap<>();

        for(StageType stage : StageType.values()){
            if(stage == StageType.BACKLOG || stage == StageType.DEPLOYMENT) {
                stages.put(stage, new StageStorage(stage, WIPLimits[stage.ordinal()]));
            }
            else {
                stages.put(stage, new StageWorking(stage, WIPLimits[stage.ordinal()]));
            }
        }

        if(PRINTINGS_RESULTS_TO_CONSOLE) {// TODO в инишник
            System.out.println("Workers: ");
            Stream.of(workers).forEach(System.out::println);
        }

        currentDay = new SimpleIntegerProperty();
        tasksDeployed = new SimpleIntegerProperty(0);
        reqSkillLevel = new SimpleDoubleProperty();

        CFD = new HashMap<>();
        leadTime = new ArrayList<>();
        cycleTime = new ArrayList<>();

        if(mainApp.showingKanbanBoard())
            cfdController.setDayTracking(currentDay, CFD);
    }

    // Запуск модели
    @Override
    public void run(){
        timeToStop.addListener((observable, oldValue, newValue) -> {
            if(newValue) { // Для рестарта
                return;
            }
        });
        // Прогоняю внешний цикл столько скольно нужно раз.
        // Считаю что цикл выполняется за день
        for(currentDay.setValue(0); currentDay.get() < NUMBER_OF_DAYS; currentDay.setValue(currentDay.get()+1)){
            if(PRINTINGS_RESULTS_TO_CONSOLE)
                System.out.println("\nDay " + currentDay + " have started =========================================================");

            outerCycle();

            // Деплой в нужные дни
            if(currentDay.get() % deploymentFrequency == 0)
                deploy();

            calculateCFDForToday();
        }

        // Возвращаю результат модели в основное приложение
        // Время задач на доске, время задач в работе, количество завершенных задач
        mainApp.addScenarioResult(
                new ResultOfModel(
                        leadTime.stream().mapToInt(Integer::intValue).sum()*1d/leadTime.size(),
                        cycleTime.stream().mapToInt(Integer::intValue).sum()*1d/cycleTime.size(),
                        tasksDeployed.get()));
        Platform.runLater(() -> currentModelFinished.setValue(true));
    }

    // Считает CFD-диаграмму в конце дня
    private void calculateCFDForToday() {
        int[] CFDForToday = new int[StageType.values().length + 1];
        CFDForToday[StageType.values().length] = tasksDeployed.get();

        for(int i=StageType.values().length-1; i>=0; --i){
            CFDForToday[i] = CFDForToday[i+1] + stages.get(StageType.values()[i]).getNumberOfTasks();
        }

        CFD.put(currentDay.getValue(), CFDForToday);
    }

    // Поставка выполненных задач
    // Сохраняет данные задач из последнего столбца и удаляет их
    private void deploy() {
        tasksDeployed.setValue(tasksDeployed.get() + stages.get(StageType.DEPLOYMENT).getNumberOfTasks());

        for(Task task : stages.get(StageType.DEPLOYMENT).getTasksToRemove()){
            leadTime.add(currentDay.get() - task.addedToStage(StageType.BACKLOG));
            cycleTime.add(task.addedToStage(StageType.DEPLOYMENT) - task.addedToStage(StageType.ANALYSIS));

            task.deploy();
            stages.get(StageType.DEPLOYMENT).removeTask(task);

            Util.sleepMilliseconds(UI_REFRESH_DELAY);
        }
    }

    // Внешний цикл
    private void outerCycle(){
        // Подготовка цикла
        reqSkillLevel.setValue(1d);
        fillBacklog();

        Stream.of(workers).forEach(Worker::refillEnergy);

        if(PRINTINGS_RESULTS_TO_CONSOLE) {
            printStages();
        }

        while(reqSkillLevel.get() > 0d && workersHaveEnergy()){
            if(!innerCycle()) // Если во внутреннем цикле не было работы, снижаю планку навыка
                reqSkillLevel.setValue(reqSkillLevel.get() - 0.05d);
        }
    }

    private void printStages() {
        for(StageType stage : StageType.values()){
            System.out.println("\nIn " + stage.toString() + ": [" + stages.get(stage).getNumberOfTasks() + "/" + stages.get(stage).getWIPLimit() + "]");
            System.out.println(stages.get(stage).composeTasksStatus());
            //System.out.println("\n");
        }
    }

    private boolean workersHaveEnergy() {
        for(Worker worker : workers){
            if(worker.getEnergy() > 0)
                return true;
        }
        return false;
    }

    private boolean innerCycle() {
        int workDoneAtThisCycle;
        boolean tasksMoved;
        workDoneAtThisCycle = makeSomeWork();
        tasksMoved = moveTasks();
        return ((workDoneAtThisCycle != 0) || tasksMoved);
    }

    private boolean moveTasks() {
        boolean tasksMoved = false;
        for(StageType stage : StageType.stagesReverse){
            switch (stage){
                case DEPLOYMENT:
                    break;
                default:
                    for(Task task : stages.get(stage).getTasksToRemove()){
                        if(task != null){
                        if(stages.get(task.getNextStage()).canAddTask()){
                            // Убираю таску с прошлой стадии
                            stages.get(stage).removeTask(task);

                            // Переношу на следующую
                            stages.get(task.getNextStage()).addTask(task);
                            task.moveToNextStage(currentDay.get());

                            Util.sleepMilliseconds(UI_REFRESH_DELAY);

                            // Записываю, что изменение было
                            tasksMoved = true;
                        }
                        }
                    }
            }
        }
        return tasksMoved;
    }

    private int makeSomeWork() {
        int amountOfWork = 0;
        for(StageType stage : StageType.workStagesReverse){

            if(stages.get(stage).getNumberOfTasks() !=0){
                for(Task task : ((StageWorking)stages.get(stage)).getTasksInWork()){
                    for(Worker worker : workers){
                        int taskCanTake   = task.getResumingWorkAtCurrentStage();       // Смотрю сколько в таске осталось работы


                        if(taskCanTake > 0                                                      // Если работа еще есть
                                && (worker.getEnergy() - worker.calculatePenalty(task)) > 0     // И у работника есть силы
                                && worker.getProductivityAtStage(stage) >= reqSkillLevel.get()){  // И он достаточно компетентен

                            worker.applyPenalty(task); // Если работник раньше не занимался этой задачей, ему нужно в ней разобраться

                            //Считаю сколько он может наработать
                            int workerCanGive = (int) (worker.getEnergy() * worker.getProductivityAtStage(stage));
                            int workDone;

                            //Выполняю возможное количество работы
                            if(workerCanGive >= taskCanTake){
                                worker.deductEnergy((int)((double)taskCanTake / worker.getProductivityAtStage(stage)));
                                task.makeSomeWork(taskCanTake);
                                workDone = taskCanTake;
                                ((StageWorking)stages.get(stage)).moveTaskToFinished(task);
                            } else {
                                worker.deductEnergy(workerCanGive);
                                task.makeSomeWork(workerCanGive);
                                workDone = workerCanGive;
                            }

                            if(workDone > 0){
                                amountOfWork += workDone;
                                Util.sleepMilliseconds(UI_REFRESH_DELAY);
                            }
                        }
                    }
                }
            }
        }

        return amountOfWork;
    }

    // Заполнение бэклога
    private void fillBacklog() {
        while (stages.get(StageType.BACKLOG).canAddTask()){
            Task newTask = Task.generateRandomTask();
            newTask.setBackLogDay(currentDay.get());
            stages.get(StageType.BACKLOG).addTask(newTask);

            if(mainApp.showingKanbanBoard())
                kanbanBoardController.watchTask(newTask);

            Util.sleepMilliseconds(UI_REFRESH_DELAY);
        }
    }

    public static int getNumberOfWorkers() {
        return NUMBER_OF_WORKERS;
    }

    public HashMap<Integer,int[]> getCFD() {
        return CFD;
    }

    public void timeToStop() {
        timeToStop.setValue(true);
    }

    public Scenario getScenario() {
        return scenario;
    }

    public BooleanProperty currentModelFinishedProperty() {
        return currentModelFinished;
    }

    public static void setPrintingsResultsToConsole(boolean printingsResultsToConsole) {
        PRINTINGS_RESULTS_TO_CONSOLE = printingsResultsToConsole;
    }
    
    public static int getNumberOfDays() {
        return NUMBER_OF_DAYS;
    }

    public static int getUiRefreshDelay() {
        return UI_REFRESH_DELAY;
    }

    public Worker[] getWorkers(){return workers;}

    public static void setNumberOfWorkers(int numberOfWorkers) {
        NUMBER_OF_WORKERS = numberOfWorkers;
    }

    public static void setNumberOfDays(int numberOfDays) {
        NUMBER_OF_DAYS = numberOfDays;
    }

    public static void setUiRefreshDelay(int tts) {
        UI_REFRESH_DELAY = tts;
    }

    public IntegerProperty currentDayProperty() {
        return currentDay;
    }

    public DoubleProperty reqSkillLevelProperty() {
        return reqSkillLevel;
    }

    public IntegerProperty tasksDeployedProperty(){
        return tasksDeployed;
    }
}
