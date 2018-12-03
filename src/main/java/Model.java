import java.util.HashMap;
import java.util.stream.Stream;

public class Model {
    private HashMap<StageType, Stage>  stages;
    private Worker[] workers;

    private static final int[] DEFAULT_WIP = {3, 3, 3, 3, 3, 3, 3, Integer.MAX_VALUE};  // TODO заполнять из файла

    public static int getNumberOfWorkers() {
        return NUMBER_OF_WORKERS;
    }

    private static final int   NUMBER_OF_WORKERS = 4;                                  // TODO заполнять из файла в мэйн, сюда передавать через конструктор
    private static final int   NUMBER_OF_STAGES = StageType.values().length;
    private static final int   NUMBER_OF_DAYS = 10;

    private double productivityLevel;   // минимум продуктивности

    Model() {
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

        System.out.println("Model contains: \nStages: ");
        Stream.of(StageType.toSortedStringArray(stages)).forEach(System.out::println);
        System.out.println("Workers: ");
        Stream.of(workers).forEach(System.out::println);
    }

    // Запуск модели
    public void start(){
        // Прогоняю внешний цикл столько скольно нужно раз.
        // Считаю что цикл выполняется за день
        for(int day=0; day < NUMBER_OF_DAYS; ++day){
            System.out.println("\nDay " + day + " have started =========================================================");
            outerCycle();
        }
    }

    // Внешний цикл
    void outerCycle(){
        // Подготовка цикла
        productivityLevel = 1d;
        fillBacklog();
        //printStages();
        System.out.println("\nIn backlog: ");
        stages.get(StageType.BACKLOG).printTasks();
        Stream.of(workers).forEach(Worker::refillEnergy);
//        Stream.of(workers).forEach(w -> System.out.print(w.getEnergy() + " "));
//        Stream.of(workers).forEach(w -> w.deductEnergy(2));
//        Stream.of(workers).forEach(w -> System.out.print(w.getEnergy() + " "));
        while(productivityLevel > 0d && workersHaveEnergy()){ // TODO пересчет энергии работников
            if(!innerCycle())
                productivityLevel -= 0.05d;
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
                            stages.get(stage).removeTask(task);
                            stages.get(task.getNextStage()).addTask(task);
                            task.moveToNextStage();
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
        for(StageType stage : StageType.stagesReverse){ // TODO заменить на проход по рабочим стадиям
                switch (stage){
                    case BACKLOG:
                    case DEPLOYMENT:
                        break;
                    default:
                        if(stages.get(stage).getNumberOfTasks() !=0){
                            for(Task task : ((StageWorking)stages.get(stage)).getTasksInWork()){
                                for(Worker worker : workers){
                                    // Смотрю сколько в таске осталось работы
                                    int taskCanTake   = task.getResumingWorkAtCurrentStage();
                                    if(taskCanTake > 0 // Если еще есть
                                            && worker.getEnergy() > 0 // И у работника есть силы
                                            && worker.getProductivityAtStage(stage) >= productivityLevel){ // И он достаточно компетентен

                                        //Считаю сколько он может наработать
                                        int workerCanGive = (int) (worker.getEnergy() * worker.getProductivityAtStage(stage)); // TODO округление в большую сторону

                                        //Выполняю возможное количество работы
                                        if(workerCanGive >= taskCanTake){
                                            worker.deductEnergy((int)((double)taskCanTake / worker.getProductivityAtStage(stage))); // TODO округление в меньшую сторону
                                            task.makeSomeWork(taskCanTake);
                                            amountOfWork += taskCanTake;
                                            ((StageWorking)stages.get(stage)).moveTaskToFinished(task); // FIXME Тут может быть порблема с перемещением.
                                        } else {
                                            worker.deductEnergy(workerCanGive);
                                            task.makeSomeWork(workerCanGive);
                                            amountOfWork += workerCanGive;
                                        }
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
            stages.get(StageType.BACKLOG).addTask(Task.generateRandomTask());
        }
    }
}
