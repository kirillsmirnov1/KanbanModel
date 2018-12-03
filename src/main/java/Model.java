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
        productivityLevel = 1d;
        fillBacklog();
        System.out.println("\nIn backlog: ");
        stages.get(StageType.BACKLOG).printTasks();
        Stream.of(workers).forEach(Worker::refillEnergy);
//        Stream.of(workers).forEach(w -> System.out.print(w.getEnergy() + " "));
//        Stream.of(workers).forEach(w -> w.deductEnergy(2));
//        Stream.of(workers).forEach(w -> System.out.print(w.getEnergy() + " "));
        // TODO закончить внешний цикл

        // TODO сделать внутренний цикл
    }

    // Заполнение бэклога
    private void fillBacklog() {
        while (stages.get(StageType.BACKLOG).canAddTask()){
            stages.get(StageType.BACKLOG).addTask(Task.generateRandomTask());
        }
    }
}
