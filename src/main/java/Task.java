import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

// Задача
public class Task {
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

    public void makeSomeWork(int work){
        stageAdvance.replace(stage, stageAdvance.get(stage) + work);
    }

    public void moveToNextStage(){
        stage = nextStage;
        do{
            nextStage = StageType.values()[nextStage.ordinal()+1];
        }while(stageCosts.get(nextStage) != 0 && nextStage != StageType.DEPLOYMENT);
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

        return new Task(RandomStringUtils.random(10, true, false), randomCosts); // TODO Заменить имя на номер
    }
}
