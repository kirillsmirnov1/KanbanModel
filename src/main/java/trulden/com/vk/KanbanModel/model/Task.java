package trulden.com.vk.KanbanModel.model;

import javafx.beans.property.*;
import trulden.com.vk.KanbanModel.model.stage.StageType;

import java.util.HashMap;
import java.util.Random;

import static trulden.com.vk.KanbanModel.model.stage.StageType.BACKLOG;
import static trulden.com.vk.KanbanModel.model.stage.StageType.DEPLOYMENT;
import static trulden.com.vk.KanbanModel.model.stage.StageType.workStages;

// Задача
public class Task {
    // Счётчик созданных задач
    private static int taskCounter;

    // Стоимость выполнения каждой стадии
    private HashMap<StageType, Integer> stagesCosts;
    // Остаток до выполнения каждой стадии
    private HashMap<StageType, Integer> stagesAdvance;

    // Название задачи
    private final String name;

    // Текущая стадия
    private ObjectProperty<StageType> stage;
    // Следующая стадия
    private StageType nextStage;

    // Флаг завершенности на текущей стадии
    private BooleanProperty doneAtCurrentStage;

    // Прогресс выполнения карточки
    private IntegerProperty totalAdvance;

    // Дни в которые карточка прибывала на стадии
    private HashMap<StageType, Integer> daysArrivedAtStages;

    // Карточка конструируется при добавлении в бэклог
    private Task(String name, HashMap<StageType, Integer> stageCosts) throws IllegalArgumentException{

        if(stageCosts.size() != StageType.values().length-2)
            throw new IllegalArgumentException("Неправильный размер массива");

        this.name = name;

        this.stagesCosts = stageCosts;
        stagesAdvance = new HashMap<>();

        stageCosts.forEach((k, v) -> stagesAdvance.put(k, 0));

        daysArrivedAtStages = new HashMap<>();

        stage = new SimpleObjectProperty<>(StageType.BACKLOG);

        nextStage = stage.get();
        calculateNextStage(); // Это нужно для сценария с непоследовательной сменой стадий (который сейчас не доступен)

        doneAtCurrentStage = new SimpleBooleanProperty(false);
        totalAdvance = new SimpleIntegerProperty(0);
    }

    // Генерирует и возвращает задачу со случайными параметрами
    public static Task generateRandomTask(){
        HashMap<StageType, Integer> randomCosts = new HashMap<>();
        for(StageType stage : workStages){
            randomCosts.put(stage, new Random().nextInt(10));
        }

        taskCounter++;

        return new Task(Integer.toString(taskCounter), randomCosts);
    }

    // Зачитывает выполненную работу в карточку
    public void makeSomeWork(int work){
        stagesAdvance.replace(stage.get(), stagesAdvance.get(stage.get()) + work);

        totalAdvance.set(totalAdvance.get() + work);

        if(getResumingWorkAtCurrentStage() == 0)
            doneAtCurrentStage.setValue(true);
    }

    // Перемещение задачи на следующую стадию
    public void moveToNextStage(int day){
        if(stage.get() != DEPLOYMENT) {
            daysArrivedAtStages.put(nextStage, day); // Сохраняю день прибытия на стадию
            stage.setValue(nextStage);
            calculateNextStage();

            if(stage.get() == DEPLOYMENT)
                doneAtCurrentStage.setValue(false);
            else
                doneAtCurrentStage.setValue(getResumingWorkAtCurrentStage() == 0);
        } else {
            doneAtCurrentStage.setValue(false);
        }
    }

    // Возвращает следующую стадию, на которую переместится карточка
    private void calculateNextStage(){
            nextStage = stage.get().nextStage();

        // Вариант, когда задача перескакивает стадии без работы
        // TODO сделать это одной из опций
//            do {
//                nextStage = nextStage.nextStage();
//                if (nextStage == StageType.DEPLOYMENT)
//                    return;
//                if (stagesCosts.get(nextStage) != 0)
//                    return;
//            } while (true);
    }

    // Возвращает количество дней, за которые задача прошла от одной стадии до другой
    private int daysFromTo(StageType from, StageType to){
        if(daysArrivedAtStages.containsKey(from) && daysArrivedAtStages.containsKey(to))
            return daysArrivedAtStages.get(to) - daysArrivedAtStages.get(from);
        return -1;
    }

    // Метит карточку выполненной в деплое
    // Использую для удаления с канбан-доски
    public void deploy(){
        if(stage.get() == DEPLOYMENT)
            doneAtCurrentStage.setValue(true);
        else
            throw new IllegalArgumentException("Can't deploy not finished task");
    }

    // Сбрасывает количество созданных задач
    public static void resetTaskCounter(){ taskCounter = 0; }

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
        if(stage.get() == StageType.DEPLOYMENT)
            return getName() + " < took " + daysFromTo(StageType.BACKLOG, StageType.DEPLOYMENT) + " days >\n" + str.toString();

        return getName() + "\n" + str.toString();
    }

    // Возвращает день добавления на стадию
    public int addedToStage(StageType stageType){
        return daysArrivedAtStages.get(stageType);
    }

    // Количество работы на стадии
    public int getWorkAtStage(StageType stage) { return stagesCosts.get(stage); }

    // Количество оставшейся работы на текущей стадии
    public int getResumingWorkAtCurrentStage(){
        if(stage.get() == BACKLOG || stage.get() == DEPLOYMENT)
            throw new IllegalArgumentException("Backlog and Deployment have no work");

        return stagesCosts.get(stage.get()) - stagesAdvance.get(stage.get());
    }

    // Геттеры и сеттеры

    public String getName() {
        return "T: " + name;
    }

    public StageType getStage() {
        return stage.get();
    }
    public StageType getNextStage() { return nextStage; }

    public ObjectProperty<StageType> stageProperty() {
        return stage;
    }

    public BooleanProperty doneAtCurrentStageProperty() {
        return doneAtCurrentStage;
    }

    public IntegerProperty totalAdvanceProperty() { return totalAdvance; }

    public void setBackLogDay(int day){
        daysArrivedAtStages.put(BACKLOG, day);
    }
}
