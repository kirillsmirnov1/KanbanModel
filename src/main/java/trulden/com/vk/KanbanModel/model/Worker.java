package trulden.com.vk.KanbanModel.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import trulden.com.vk.KanbanModel.model.stage.StageType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static trulden.com.vk.KanbanModel.model.stage.StageType.workStages;

//TODO обучение сотрудников

// Класс сотрудника
public class Worker {
    private static int workerCounter = 0;

    private HashMap<StageType, Double> productivityAtStage;

    public IntegerProperty energyProperty() {
        return energy;
    }

    private IntegerProperty energy;
    private final int id;
    private final String name;

    private String lastTask = "";

    private static int MAX_ENERGY; // Максимальное количество энергии
    private static double TASK_CHANGE_PENALTY;  // Штраф за смену задачи (энергия которая тратится на то чтобы войти в курс дела)

    public void applyPenalty(Task task){
        energy.set(energy.get() - calculatePenalty(task));
        setLastTask(task);
    }

    public int calculatePenalty(Task task){
        return sameTaskAsLastOne(task) ? 0 : (int) (MAX_ENERGY * TASK_CHANGE_PENALTY);
    }

    private boolean sameTaskAsLastOne(Task task){
        return lastTask.equals(task.getName());
    }

    private void setLastTask(Task task){
        lastTask = task.getName();
    }

    public static void setMaxEnergy(int me){
        MAX_ENERGY = me;
    }

    public static void setTaskChangePenalty(double tcp){
        TASK_CHANGE_PENALTY = tcp;
    }

    private Worker(String name, int id, HashMap<StageType, Double> productivityAtStage){

        energy = new SimpleIntegerProperty(0);

        this.name = name;
        this.id = id;
        this.productivityAtStage = productivityAtStage;
    }

    public static Worker generateRandomWorker(String name) {

        if(name == null || name.length() == 0){
            throw new IllegalArgumentException("Worker's name must not be null or empty");
        }

        HashMap<StageType, Double> randomProductivity = new HashMap<>();
        for (StageType stage : workStages) {
            randomProductivity.put(stage, new Random().nextDouble());
        }

        return new Worker(name, workerCounter++, randomProductivity);
    }


    @Override
    public String toString(){
        return "W " + id + " : " + name + " [" + energy.get() +  "]\npr: " + Arrays.toString(StageType.toSortedStringArray(productivityAsPercents()));
    }

    private HashMap<StageType, Integer> productivityAsPercents(){
        HashMap<StageType, Integer> percents = new HashMap<>();

        for(StageType stage : workStages){
            percents.put(stage, (int)(productivityAtStage.get(stage) * 100));
        }

        return percents;
    }

    public void refillEnergy(){
        energy.setValue(MAX_ENERGY);
    }

    public int getEnergy() {
        return energy.get();
    }

    public void deductEnergy(int deductedEnergy){
        energy.setValue(energy.get() - deductedEnergy);
    }

    public double getProductivityAtStage(StageType stage) {
        return productivityAtStage.get(stage);
    }

    public int getID() {
        return id;
    }
}
