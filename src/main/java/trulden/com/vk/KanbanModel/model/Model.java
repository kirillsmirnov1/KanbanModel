package trulden.com.vk.KanbanModel.model;

import trulden.com.vk.KanbanModel.MainApp;
import trulden.com.vk.KanbanModel.util.Util;
import trulden.com.vk.KanbanModel.view.MainWindowController;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Model implements Runnable{
   MainWindowController mwc;

    private HashMap<StageType, Stage>  stages;
    private Worker[] workers;

    private static int[] DEFAULT_WIP = {3, 3, 3, 3, 3, 3, 3, Integer.MAX_VALUE};
    private static int   NUMBER_OF_WORKERS = 5;
    private static int   NUMBER_OF_DAYS = 50;
    private int          timeToSleep = 50;

    private double productivityLevel;   // минимум продуктивности
    private int    currentDay;

    public static void setDefaultWip(int[] defaultWip) {
        DEFAULT_WIP = defaultWip;
    }

    public static void setNumberOfWorkers(int numberOfWorkers) {
        NUMBER_OF_WORKERS = numberOfWorkers;
    }

    public static void setNumberOfDays(int numberOfDays) {
        NUMBER_OF_DAYS = numberOfDays;
    }

    public void setTimeToSleep(int timeToSleep) {
        this.timeToSleep = timeToSleep;
    }

    public Model(MainWindowController mwc) {
        this.mwc = mwc;

        stages  = new HashMap<>();
        workers = new Worker[NUMBER_OF_WORKERS];

        for(StageType stage : StageType.values()){
            if(stage == StageType.BACKLOG || stage == StageType.DEPLOYMENT)
                stages.put(stage, new StageStorage(stage, DEFAULT_WIP[stage.ordinal()]));
            else
                stages.put(stage, new StageWorking(stage, DEFAULT_WIP[stage.ordinal()]));
        }

        for(int i = 0; i < workers.length; ++i){
            workers[i] = Worker.generateRandomWorker();
        }

        System.out.println("Workers: ");
        Stream.of(workers).forEach(System.out::println);
    }

    // Запуск модели
    @Override
    public void run(){
        // Прогоняю внешний цикл столько скольно нужно раз.
        // Считаю что цикл выполняется за день
        for(currentDay = 0; currentDay < NUMBER_OF_DAYS; ++currentDay){
            System.out.println("\nDay " + currentDay + " have started =========================================================");
            outerCycle();
        }
    }

    // Внешний цикл
    private void outerCycle(){
        // Подготовка цикла
        productivityLevel = 1d;
        fillBacklog();
        printStages();

        Stream.of(workers).forEach(Worker::refillEnergy);

        while(productivityLevel > 0d && workersHaveEnergy()){
            if(!innerCycle())
                productivityLevel -= 0.05d;
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
                            mwc.removeTask(task, task.getStage());

                            // Переношу на следующую
                            stages.get(task.getNextStage()).addTask(task);
                            task.moveToNextStage(currentDay);
                            mwc.addTask(task.toString(), task.getStage());

                            Util.sleepMilliseconds(timeToSleep);

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
                                && worker.getProductivityAtStage(stage) >= productivityLevel){  // И он достаточно компетентен

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
                                mwc.updateTask(task, task.getStage());
                                Util.sleepMilliseconds(timeToSleep);
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
            Task newTask = Task.generateRandomTask(currentDay);
            stages.get(StageType.BACKLOG).addTask(newTask);
            mwc.addTask(newTask.toString(), StageType.BACKLOG);

            Util.sleepMilliseconds(timeToSleep);
        }
    }

    public static int getNumberOfWorkers() {
        return NUMBER_OF_WORKERS;
    }
}
