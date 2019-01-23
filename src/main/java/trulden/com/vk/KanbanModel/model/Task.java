package trulden.com.vk.KanbanModel.model;

import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Random;

import static trulden.com.vk.KanbanModel.model.StageType.BACKLOG;
import static trulden.com.vk.KanbanModel.model.StageType.DEPLOYMENT;
import static trulden.com.vk.KanbanModel.model.StageType.workStages;

// Задача
public class Task {
    private static int taskCounter = 0;

    private HashMap<StageType, Integer> stagesCosts;   // Стоимость выполнения каждой стадии
    private HashMap<StageType, Integer> stagesAdvance; // Остаток до выполнения каждой стадии

    private final String namePrefix = "T: ";
    private final String name;       // Название задачи

    private StageType stage;        // Текущая стадия
    private StageType nextStage;    // Следующая стадия

    private BooleanProperty doneAtCurrentStage;
    private IntegerProperty totalAdvance;
    
    private HashMap<StageType, Integer> daysAtStages; // Дни в которые карточка прибывала на стадии

    // Карточка конструируется при добавлении в бэклог
    Task(String name, HashMap<StageType, Integer> stageCosts, int day) throws IllegalArgumentException{

        if(stageCosts.size() != StageType.values().length-2)
            throw new IllegalArgumentException("Неправильный размер массива");

        this.name = name;

        this.stagesCosts = stageCosts;
        stagesAdvance = new HashMap<>();

        stageCosts.forEach((k, v) -> stagesAdvance.put(k, 0));

        daysAtStages = new HashMap<>();

        stage = StageType.BACKLOG;

        daysAtStages.put(stage, day);

        nextStage = stage;
        calculateNextStage(); // Это нужно для сценария с непоследовательной сменой стадий

        doneAtCurrentStage = new SimpleBooleanProperty(false);
        totalAdvance = new SimpleIntegerProperty(0);
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
        if(stage == BACKLOG || stage == DEPLOYMENT)
            return 0;

        return stagesCosts.get(stage) - stagesAdvance.get(stage);
    }
    public int getWorkAtStage(StageType stage) { return stagesCosts.get(stage); }

    public void makeSomeWork(int work){
        stagesAdvance.replace(stage, stagesAdvance.get(stage) + work);

        totalAdvance.set(totalAdvance.get() + work);

        if(getResumingWorkAtCurrentStage() == 0)
            doneAtCurrentStage.setValue(true);
    }

    public void moveToNextStage(int day){
        if(stage != StageType.DEPLOYMENT) {
            daysAtStages.put(nextStage, day);
            stage = nextStage;
            calculateNextStage();
        }

        doneAtCurrentStage.setValue(getResumingWorkAtCurrentStage() == 0);
    }

    private void calculateNextStage(){
            nextStage = stage.nextStage();

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
        if(stage == StageType.DEPLOYMENT)
            return getName() + " < took " + daysFromTo(StageType.BACKLOG, StageType.DEPLOYMENT) + " days >\n" + str.toString();

        return getName() + "\n" + str.toString();
    }

    public int daysFromTo(StageType from, StageType to){
        if(daysAtStages.containsKey(from) && daysAtStages.containsKey(to))
            return daysAtStages.get(to) - daysAtStages.get(from);
        return -1;
    }

    public static Task generateRandomTask(int day){
        HashMap<StageType, Integer> randomCosts = new HashMap<>();
        for(StageType stage : workStages){
            randomCosts.put(stage, new Random().nextInt(10));
        }

        taskCounter++;

        //return new Task(RandomStringUtils.random(10, true, false), randomCosts);
        return new Task(Integer.toString(taskCounter), randomCosts, day);
    }

    public String getName() {
        return namePrefix + name;
    }
}
