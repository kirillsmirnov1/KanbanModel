import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

// Задача
public class Task {
    private static int TaskCounter = 0;

    private HashMap<StageType, Integer> stageCosts;   // Стоимость выполнения каждой стадии
    private HashMap<StageType, Integer> stageAdvance; // Остаток до выполнения каждой стадии

    private final String name;       // Название задачи

    private StageType stage;        // Текущая стадия
    private StageType nextStage;    // Следующая стадия

    // Карточка конструируется при добавлении в бэклог
    Task(String name, HashMap<StageType, Integer> stageCosts) throws IllegalArgumentException{

        if(stageCosts.size() != StageType.values().length-2)
            throw new IllegalArgumentException("Неправильный размер массива");

        this.stageCosts = stageCosts;
        stageAdvance = new HashMap<>();

        stageCosts.forEach((k, v) -> stageAdvance.put(k, 0));

        this.name = name;

        stage = StageType.BACKLOG;
        calculateNextStage();
    }

    // Возвращает стадию на которой сейчас находится карточка
    public StageType getStage() {
        return stage;
    }
    public StageType getNextStage() { return nextStage; }

    public boolean canGoToNextStage(){
       return (getResumingWorkAtCurrentStage() == 0);
    }

    public int getResumingWorkAtCurrentStage(){
        return stageCosts.get(stage) - stageAdvance.get(stage);
    }

    public void makeSomeWork(int work){ // TODO добавить проверку на то что значение корректно
        stageAdvance.replace(stage, stageAdvance.get(stage) + work);
    }

    public void moveToNextStage(){
        stage = nextStage;
        calculateNextStage();
    }

    private void calculateNextStage(){
        if(stage != StageType.DEPLOYMENT) {
            nextStage = StageType.values()[stage.ordinal() + 1];
            while (true) {
                if (nextStage == StageType.DEPLOYMENT)
                    return;
                if (stageCosts.get(nextStage) != 0)
                    return;
                nextStage = StageType.values()[nextStage.ordinal() + 1];
            }
        }
    }

    @Override
    public String toString(){ // TODO вывод выполнения в процентах
        return "T: " + name + ", costs: " + Arrays.toString(StageType.toSortedStringArray(stageCosts));
    }

    static Task generateRandomTask(){
        HashMap<StageType, Integer> randomCosts = new HashMap<>();
        for(StageType stage : StageType.workStages){
            randomCosts.put(stage, new Random().nextInt(10));
        }

        TaskCounter++;

        //return new Task(RandomStringUtils.random(10, true, false), randomCosts);
        return new Task(Integer.toString(TaskCounter), randomCosts);
    }
}
