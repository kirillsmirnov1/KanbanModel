import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

public class Model {
    private HashMap<WorkStages, Stage>  stages;
    private Worker[] workers;

    private static final int[] DEFAULT_WIP = {3, 3, 3, 3, 3, 3, 3, Integer.MAX_VALUE};  // TODO заполнять из файла
    private static final int   NUMBER_OF_WORKERS = 13;                                  // TODO заполнять из файла
    private static final int   NUMBER_OF_STAGES = WorkStages.values().length;
    private static final int   NUMBER_OF_DAYS = 10;

    private double productivityLevel;   // минимум продуктивности


    Model() {
        stages  = new HashMap<>();
        workers = new Worker[NUMBER_OF_WORKERS];

        for(WorkStages stage : WorkStages.values()){
            if(stage == WorkStages.BACKLOG || stage == WorkStages.DEPLOYMENT)
                stages.put(stage, new StageStorage(stage, DEFAULT_WIP[stage.ordinal()]));
            else
                stages.put(stage, new StageWorking(stage, DEFAULT_WIP[stage.ordinal()]));
        }

        String[] animalNames = getAnimalNames(NUMBER_OF_WORKERS);

        for(int i = 0; i < workers.length; ++i){
            workers[i] = generateNamedWorker(animalNames[i]);
        }

        System.out.println("Model contains: \nStages: ");
        Stream.of(WorkStages.toSortedStringArray(stages)).forEach(System.out::println);
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
        stages.get(WorkStages.BACKLOG).printTasks();
    }

    // Заполнение бэклога
    private void fillBacklog() {
        while (stages.get(WorkStages.BACKLOG).canAddTask()){
            stages.get(WorkStages.BACKLOG).addTask(generateRandomTask());
        }
    }

    // Создание задачи со случайным названием и стоимостью
    private Task generateRandomTask() {
        return new Task(RandomStringUtils   // рандомное имя TODO брать осмысленные имена
                .random(10, true, false),
                generateRandomCosts());
    }

    // Создание случайных стоимостей для задачи
    private int[] generateRandomCosts() { // TODO перенести в конструктор таски
        int[] productivity = new Random().ints(NUMBER_OF_STAGES, 0, 10).toArray();
        productivity[0] = 0;
        productivity[NUMBER_OF_STAGES - 1] = 0;

        return productivity;
    }

    // Создание работника со случайным именем и продуктивностью
    private Worker generateRandomWorker() { // TODO считывание работников из файла
        return new Worker(                  // TODO XML структура файла работников
                RandomStringUtils   // рандомное имя
                        .random(10, true, false),
                        generateRandomProductivity()
                );
    }

    // Создание сотрудника с именем и произвольной продуктивностью
    private Worker generateNamedWorker(String name) {
        return new Worker(
                name,
                generateRandomProductivity()
        );
    }

    // Создание массива произвольной продуктивности
    private double[] generateRandomProductivity() {
        double[] productivity = new Random().doubles().limit(NUMBER_OF_STAGES).toArray();
        productivity[0] = 0d;
        productivity[NUMBER_OF_STAGES - 1] = 0d;

        return productivity;
    }

    // Получить массив звериных имен
    private String[] getAnimalNames(int numberOfNames) {
        String[] names = new String[numberOfNames];
        int[] lineNumbers;
        int numberOfLines;
        int lineCounter = 1;

        try {
            BufferedReader br = new BufferedReader(new FileReader("animals.txt"));
            String line = br.readLine();
            numberOfLines = Integer.parseInt(line);
            lineNumbers = new Random().ints(1, numberOfLines).limit(NUMBER_OF_WORKERS).sorted().toArray();

            for (int i = 0; i < NUMBER_OF_WORKERS; ++i) {
                while (lineCounter < lineNumbers[i] - 1) {
                    br.readLine();
                    lineCounter++;
                }
                names[i] = br.readLine();
                lineCounter++;
            }
        } catch (IOException e){ // TODO нормальный отлов исключений
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return names;
    }
}
