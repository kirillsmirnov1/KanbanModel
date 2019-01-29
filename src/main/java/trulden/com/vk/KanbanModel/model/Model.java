package trulden.com.vk.KanbanModel.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import trulden.com.vk.KanbanModel.util.Util;
import trulden.com.vk.KanbanModel.view.MainWindowController;

import java.util.HashMap;
import java.util.stream.Stream;

public class Model implements Runnable{
    private MainWindowController mwc;

    private HashMap<StageType, Stage>  stages;
    private Worker[] workers;
    private Task[]   bigPileOfTasks;

    private HashMap<Integer, int[]> CFD;

    public  static int[] DEFAULT_WIP;
    private static int   NUMBER_OF_WORKERS;
    private static int   NUMBER_OF_DAYS;
    private static int   TIME_TO_SLEEP;
    private static int   DEPLOYMENT_FREQUENCY;

    private DoubleProperty  productivityLevel;   // минимум продуктивности
    private IntegerProperty currentDay;
    private IntegerProperty tasksDeployed;

    private int tasksInitiated = 0;

    private static final boolean CONSOLE_LOG = false;

    public static int getNumberOfDays() {
        return NUMBER_OF_DAYS;
    }

    public Worker[] getWorkers(){return workers;}

    public static void setDefaultWip(int[] defaultWip) {
        DEFAULT_WIP = defaultWip;
    }

    public static void setNumberOfWorkers(int numberOfWorkers) {
        NUMBER_OF_WORKERS = numberOfWorkers;
    }

    public static void setNumberOfDays(int numberOfDays) {
        NUMBER_OF_DAYS = numberOfDays;
    }

    public static void setTimeToSleep(int tts) {
        TIME_TO_SLEEP = tts;
    }

    public static void setDeploymentFrequency(int deploymentFrequency) {
        DEPLOYMENT_FREQUENCY = deploymentFrequency;
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

    public Model(MainWindowController mwc, Worker[] workers, Task[] tasks) {
        this.mwc = mwc;
        this.workers = workers;
        bigPileOfTasks = tasks;

        stages  = new HashMap<>();

        for(StageType stage : StageType.values()){
            if(stage == StageType.BACKLOG || stage == StageType.DEPLOYMENT) {
                stages.put(stage, new StageStorage(stage, DEFAULT_WIP[stage.ordinal()]));
            }
            else {
                stages.put(stage, new StageWorking(stage, DEFAULT_WIP[stage.ordinal()]));
            }
        }

        if(CONSOLE_LOG) {
            System.out.println("Workers: ");
            Stream.of(workers).forEach(System.out::println);
        }

        currentDay = new SimpleIntegerProperty();
        tasksDeployed = new SimpleIntegerProperty(0);
        productivityLevel = new SimpleDoubleProperty();

        CFD = new HashMap<>();
    }

    // Запуск модели
    @Override
    public void run(){
        // Прогоняю внешний цикл столько скольно нужно раз.
        // Считаю что цикл выполняется за день
        for(currentDay.setValue(0); currentDay.get() < NUMBER_OF_DAYS; currentDay.setValue(currentDay.get()+1)){
            if(CONSOLE_LOG)
                System.out.println("\nDay " + currentDay + " have started =========================================================");

            outerCycle();

            if(currentDay.get() % DEPLOYMENT_FREQUENCY == 0)
                deploy();

            calculateCFDForToday();
        }
    }

    private void calculateCFDForToday() {
        int[] CFDForToday = new int[StageType.values().length + 1];
        CFDForToday[StageType.values().length] = tasksDeployed.get();

        for(int i=StageType.values().length-1; i>=0; --i){
            CFDForToday[i] = CFDForToday[i+1] + stages.get(StageType.values()[i]).getNumberOfTasks();
        }

        CFD.put(currentDay.getValue(), CFDForToday);
    }

    private void deploy() {
        tasksDeployed.setValue(tasksDeployed.get() + stages.get(StageType.DEPLOYMENT).getNumberOfTasks());
        for(Task task : stages.get(StageType.DEPLOYMENT).getTasksToRemove()){
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
                                && worker.getEnergy() > 0                                       // И у работника есть силы
                                && worker.getProductivityAtStage(stage) >= productivityLevel.get()){  // И он достаточно компетентен

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
            mwc.watchTask(newTask);

            Util.sleepMilliseconds(TIME_TO_SLEEP);
        }
    }

    public static int getNumberOfWorkers() {
        return NUMBER_OF_WORKERS;
    }

    public HashMap<Integer,int[]> getCFD() {
        return CFD;
    }
}
