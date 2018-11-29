import java.util.Arrays;

// Задача
public class Task {
    private final int[] stageCosts;  // Стоимость выполнения каждой стадии // TODO в хэшмэп
    private int[] stageAdvance;      // Остаток до выполнения каждой стадии // TODO в хэшмэп

    private final String name;       // Название задачи

    private WorkStages stage;        // Текущая стадия
    private WorkStages nextStage;    // Следующая стадия

    // Карточка конструируется при добавлении в бэклог
    Task(String name, int[] stageCosts) throws IllegalArgumentException{

        if(stageCosts.length != WorkStages.values().length)
            throw new IllegalArgumentException("Неправильный размер массива");

        if(stageCosts[WorkStages.BACKLOG.ordinal()] != 0 ||
           stageCosts[WorkStages.DEPLOYMENT.ordinal()] != 0)
            throw new IllegalArgumentException("Первый и последний столбцы не могут иметь нагрузку");

        this.stageCosts = stageCosts;
        this.stageAdvance = new int[stageCosts.length];

        this.name = name;

        stage = WorkStages.BACKLOG;
    }

    // Возвращает стадию на которой сейчас находится карточка
    public WorkStages getStage() {
        return stage;
    }
    public WorkStages getNextStage() { return nextStage; }

    public boolean canGoToNextStage(){
       return (getResumingWorkAtCurrentStage() == 0);
    }

    public int getResumingWorkAtCurrentStage(){
        return stageCosts[stage.ordinal()] - stageAdvance[stage.ordinal()];
    }

    public void makeSomeWork(int work){
        stageAdvance[stage.ordinal()] += work;
    }

    public void moveToNextStage(){
        stage = nextStage;
        do{
            nextStage = WorkStages.values()[nextStage.ordinal()+1];
        }while(stageCosts[nextStage.ordinal()] != 0 && nextStage != WorkStages.DEPLOYMENT);
    }

    @Override
    public String toString(){ // TODO вывод выполнения в процентах
        return "T: " + name + ", costs: " + Arrays.toString(stageCosts);
    }
}
