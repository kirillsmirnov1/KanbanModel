package trulden.com.vk.KanbanModel.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import trulden.com.vk.KanbanModel.MainApp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static trulden.com.vk.KanbanModel.model.StageType.workStages;

public class Worker {
    private static int workerCounter = 0;

    private HashMap<StageType, Double> productivityAtStage;

    public IntegerProperty energyProperty() {
        return energy;
    }

    private IntegerProperty energy;
    private final int id;
    private final String name;

    private static int MAX_ENERGY; 

    public static void setMaxEnergy(int me){
        MAX_ENERGY = me;
    }

    private Worker(String name, int id, HashMap<StageType, Double> productivityAtStage){

        if(productivityAtStage.size() != workStages.length)
            throw new IllegalArgumentException("Неправильный размер массива");

        energy = new SimpleIntegerProperty(0);

        this.name = name;
        this.id = id;
        this.productivityAtStage = productivityAtStage;
    }

    public static Worker generateRandomWorker(String name) {
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
