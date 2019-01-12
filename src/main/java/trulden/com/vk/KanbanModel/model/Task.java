package trulden.com.vk.KanbanModel.model;

import java.util.HashMap;
import java.util.Random;

// Задача
public class Task {
    private static int taskCounter = 0;

    private HashMap<StageType, Integer> stagesCosts;   // Стоимость выполнения каждой стадии
    private HashMap<StageType, Integer> stagesAdvance; // Остаток до выполнения каждой стадии

    private final String namePrefix = "T: ";
    private final String name;       // Название задачи

    private StageType stage;        // Текущая стадия
    private StageType nextStage;    // Следующая стадия

    // Карточка конструируется при добавлении в бэклог
    Task(String name, HashMap<StageType, Integer> stageCosts) throws IllegalArgumentException{

        if(stageCosts.size() != StageType.values().length-2)
            throw new IllegalArgumentException("Неправильный размер массива");

        this.stagesCosts = stageCosts;
        stagesAdvance = new HashMap<>();

        stageCosts.forEach((k, v) -> stagesAdvance.put(k, 0));

        this.name = name;

        stage = StageType.BACKLOG;
        nextStage = stage;
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
        return stagesCosts.get(stage) - stagesAdvance.get(stage);
    }

    public void makeSomeWork(int work){
        stagesAdvance.replace(stage, stagesAdvance.get(stage) + work);
    }

    public void moveToNextStage(){
        stage = nextStage;
        calculateNextStage();
    }

    private void calculateNextStage(){
        if(stage != StageType.DEPLOYMENT) {
            nextStage = stage.nextStage();
        }

        // Вариант когда задача сразу переходит на нужную стадию
        // Потом нужно сделать это одной из опций
//            do {
//                nextStage = nextStage.nextStage();
//                if (nextStage == StageType.DEPLOYMENT)
//                    return;
//                if (stagesCosts.get(nextStage) != 0)
//                    return;
//            } while (true);
    }

    @Override
    public String toString(){
        String[] costs = StageType.toSortedStringArray(stagesCosts);
        String[] advance = StageType.toSortedStringArray(stagesAdvance);

        StringBuilder str = new StringBuilder("[ ");

        for(int i = 0; i < costs.length; ++i){
            str.append(advance[i]).append("/").append(costs[i]).append(", ");
        }
        str.delete(str.lastIndexOf(","), str.lastIndexOf(",") + 1);
        str.append("]");

        //return namePrefix + name + ", costs: " + str.toString();
        return getName() + "\n" + str.toString();
    }

    public static Task generateRandomTask(){
        HashMap<StageType, Integer> randomCosts = new HashMap<>();
        for(StageType stage : StageType.workStages){
            randomCosts.put(stage, new Random().nextInt(10));
        }

        taskCounter++;

        //return new Task(RandomStringUtils.random(10, true, false), randomCosts);
        return new Task(Integer.toString(taskCounter), randomCosts);
    }

    public String getName() {
        return namePrefix + name;
    }
}
