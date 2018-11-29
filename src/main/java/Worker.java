public class Worker {
    private final double[] productivityAtStage;
    private int energy;

    private final String name;
    private static final int MAX_ENERGY = 10;


    Worker(String name, double[] productivityAtStage){

        if(productivityAtStage.length != WorkStages.values().length)
            throw new IllegalArgumentException("Неправильный размер массива");

        if(productivityAtStage[WorkStages.BACKLOG.ordinal()] != 0 ||
           productivityAtStage[WorkStages.DEPLOYMENT.ordinal()] != 0)
            throw new IllegalArgumentException("Первый и последний столбцы не могут иметь нагрузку");


        this.name = name;
        this.productivityAtStage = productivityAtStage;
    }

    @Override
    public String toString(){
        return "W: " + name;// + " pr: " + java.util.Arrays.toString(productivityAtStage);
    }

    public void refillEnergy(){
        energy = MAX_ENERGY;
    }
}
