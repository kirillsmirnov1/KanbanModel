package trulden.com.vk.KanbanModel.model;

import trulden.com.vk.KanbanModel.MainApp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Worker {
    private static int workerCounter = 0;

    private HashMap<StageType, Double> productivityAtStage;
    private int energy;
    private final int id;
    private final String name;

    private static final int MAX_ENERGY = 10;

    Worker(String name, int id, HashMap<StageType, Double> productivityAtStage){

        if(productivityAtStage.size() != StageType.workStages.length)
            throw new IllegalArgumentException("Неправильный размер массива");

        this.name = name;
        this.id = id;
        this.productivityAtStage = productivityAtStage;
    }

    public static Worker generateRandomWorker() {
        HashMap<StageType, Double> randomProductivity = new HashMap<>();
        for (StageType stage : StageType.workStages) {
            randomProductivity.put(stage, new Random().nextDouble());
        }

        //return new Task(RandomStringUtils.random(10, true, false), randomCosts);
        return new Worker(MainApp.workerNames[workerCounter], workerCounter++, randomProductivity);
    }


    @Override
    public String toString(){
        return "W " + id + " : " + name + " pr: " + Arrays.toString(StageType.toSortedStringArray(productivityAtStage));
    }

    public void refillEnergy(){
        energy = MAX_ENERGY;
    }

    public int getEnergy() {
        return energy;
    }

    public void deductEnergy(int deductedEnergy){
        energy -= deductedEnergy;
    }

    public double getProductivityAtStage(StageType stage) {
        return productivityAtStage.get(stage);
    }
}
