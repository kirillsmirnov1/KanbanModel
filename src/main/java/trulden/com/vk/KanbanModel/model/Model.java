package trulden.com.vk.KanbanModel.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import trulden.com.vk.KanbanModel.MainApp;
import trulden.com.vk.KanbanModel.util.Scenario;
import trulden.com.vk.KanbanModel.util.Util;
import trulden.com.vk.KanbanModel.view.CFDController;
import trulden.com.vk.KanbanModel.view.KanbanBoardController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class Model implements Runnable{
    private final Scenario scenario;
    private final MainApp mainApp;
    private KanbanBoardController kanbanBoardController;
    private BooleanProperty currentModelFinished = new SimpleBooleanProperty(false);

    private HashMap<StageType, Stage>  stages;
    private Worker[] workers;
    private Task[]   bigPileOfTasks;

    private HashMap<Integer, int[]> CFD;
    private ArrayList<Integer> leadTime;
    private ArrayList<Integer> cycleTime;

    private static int   NUMBER_OF_WORKERS;
    private static int   NUMBER_OF_DAYS;
    private static int   TIME_TO_SLEEP;

    private int   deploymentFrequency;
    public  int[] defaultWip;

    private DoubleProperty  productivityLevel;   // минимум продуктивности
    private IntegerProperty currentDay;
    private IntegerProperty tasksDeployed;

    private int tasksInitiated = 0;

    private static final boolean CONSOLE_LOG = false;

    private BooleanProperty timeToStop = new SimpleBooleanProperty(false);

    public static int getNumberOfDays() {
        return NUMBER_OF_DAYS;
    }

    public static int getTimeToSleep() {
        return TIME_TO_SLEEP;
    }

    public Worker[] getWorkers(){return workers;}

    public static void setNumberOfWorkers(int numberOfWorkers) {
        NUMBER_OF_WORKERS = numberOfWorkers;
    }

    public static void setNumberOfDays(int numberOfDays) {
        NUMBER_OF_DAYS = numberOfDays;
    }

    public static void setTimeToSleep(int tts) {
        TIME_TO_SLEEP = tts;
    }

    public IntegerProperty currentDayProperty() {
        return currentDay;
    }

    public DoubleProperty productivityLevelProperty() {
        return productivityLevel;
    }

    public IntegerProperty tasksDeployedProperty(){
        return tasksDeployed;
    }


    public Model(MainApp mainApp, KanbanBoardController kanbanBoardController, CFDController cfdController, Scenario scenario, Worker[] workers, Task[] tasks) {
        this.mainApp = mainApp;
        this.kanbanBoardController = kanbanBoardController;
        this.workers = workers;
        bigPileOfTasks = tasks;
        this.scenario = scenario;

        defaultWip = scenario.getDefaultWIP();
        deploymentFrequency = scenario.getDeploymentFrequency();
        Worker.setMaxEnergy(scenario.getMaxWorkerEnergy());
        Worker.setTaskChangePenalty(scenario.getTaskChangePenalty());

        stages  = new HashMap<>();

        for(StageType stage : StageType.values()){
            if(stage == StageType.BACKLOG || stage == StageType.DEPLOYMENT) {
                stages.put(stage, new StageStorage(stage, defaultWip[stage.ordinal()]));
            }
            else {
                stages.put(stage, new StageWorking(stage, defaultWip[stage.ordinal()]));
            }
        }

        if(CONSOLE_LOG) {// TODO в инишник
            System.out.println("Workers: ");
            Stream.of(workers).forEach(System.out::println);
        }

        currentDay = new SimpleIntegerProperty();
        tasksDeployed = new SimpleIntegerProperty(0);
        productivityLevel = new SimpleDoubleProperty();

        CFD = new HashMap<>();
        leadTime = new ArrayList<>();
        cycleTime = new ArrayList<>();

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
            if(CONSOLE_LOG)
                System.out.println("\nDay " + currentDay + " have started =========================================================");

            outerCycle();

            if(currentDay.get() % deploymentFrequency == 0)
                deploy();

            calculateCFDForToday();
        }

        mainApp.addModelResult(
                new ResultOfModel(
                        leadTime.stream().mapToInt(i -> i.intValue()).sum()*1d/leadTime.size(),
                        cycleTime.stream().mapToInt(i -> i.intValue()).sum()*1d/cycleTime.size(),
                        tasksDeployed.get()));
        Platform.runLater(() -> currentModelFinished.setValue(true));
    }

    private void calculateCFDForToday() {
        int[] CFDForToday = new int[StageType.values().length + 1];
        CFDForToday[StageType.values().length] = tasksDeployed.get();

        for(int i=StageType.values().length-1; i>=0; --i){
            CFDForToday[i] = CFDForToday[i+1] + stages.get(StageType.values()[i]).getNumberOfTasks();
        }

        CFD.put(currentDay.getValue(), CFDForToday);
    }

    // Поставка выполненных задач
    private void deploy() {
        tasksDeployed.setValue(tasksDeployed.get() + stages.get(StageType.DEPLOYMENT).getNumberOfTasks());
        for(Task task : stages.get(StageType.DEPLOYMENT).getTasksToRemove()){
            leadTime.add(currentDay.get() - task.addedToStage(StageType.BACKLOG));
            cycleTime.add(task.addedToStage(StageType.DEPLOYMENT) - task.addedToStage(StageType.ANALYSIS));
            task.deploy();
            stages.get(StageType.DEPLOYMENT).removeTask(task);
            Util.sleepMilliseconds(TIME_TO_SLEEP);
        }
    }

    // Внешний цикл
    private void outerCycle(){
        // Подготовка цикла
        productivityLevel.setValue(1d);
        fillBacklog();

        if(CONSOLE_LOG)
            printStages();

        Stream.of(workers).forEach(Worker::refillEnergy);

        while(productivityLevel.get() > 0d && workersHaveEnergy()){
            if(!innerCycle())
                productivityLevel.setValue(productivityLevel.get() - 0.05d);
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

                            Util.sleepMilliseconds(TIME_TO_SLEEP);

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
                                && worker.getProductivityAtStage(stage) >= productivityLevel.get()){  // И он достаточно компетентен

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
                                Util.sleepMilliseconds(TIME_TO_SLEEP);
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
            Task newTask = bigPileOfTasks[tasksInitiated++];
            newTask.setBackLogDay(currentDay.get());
            stages.get(StageType.BACKLOG).addTask(newTask);

            if(mainApp.getShowBoard())
                kanbanBoardController.watchTask(newTask);

            Util.sleepMilliseconds(TIME_TO_SLEEP);
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
}
