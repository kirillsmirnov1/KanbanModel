package trulden.com.vk.KanbanModel.util;


public class Scenario {
    //private boolean allRoundEmployees   = true;     // TODO
    private boolean linearTasksMovement = true;
    //private boolean workersNeedToRest   = false;    // TODO
    //private boolean performCardsFromRightToLeft = true; // TODO

    //private CardPriority cardPriority = CardPriority.OLD; // TODO

    private int    deploymentFrequency = 7;
    private int    maxWorkerEnergy = 8;
    private double taskChangePenalty = 0.2;
    private int[]  defaultWIP = {3, 3, 3, 3, 3, 3, 3, 1000000};

    // Зона геттеров и сеттеров

    public boolean isLinearTasksMovement() {
        return linearTasksMovement;
    } // TODO

    public void setLinearTasksMovement(boolean linearTasksMovement) {
        this.linearTasksMovement = linearTasksMovement;
    }

    public int getDeploymentFrequency() {
        return deploymentFrequency;
    }

    public void setDeploymentFrequency(int deploymentFrequency) {
        this.deploymentFrequency = deploymentFrequency;
    }

    public int[] getDefaultWIP() {
        return defaultWIP;
    }

    public void setDefaultWIP(int[] defaultWIP) {
        this.defaultWIP = defaultWIP;
    }

    public int getMaxWorkerEnergy() {
        return maxWorkerEnergy;
    }

    public void setMaxWorkerEnergy(int maxWorkerEnergy) {
        this.maxWorkerEnergy = maxWorkerEnergy;
    }

    public double getTaskChangePenalty() {
        return taskChangePenalty;
    }

    public void setTaskChangePenalty(double taskChangePenalty) {
        this.taskChangePenalty = taskChangePenalty;
    }

    // Приоритет выбора карточки для выполнения

//    public enum  CardPriority {
//        OLD,    // Самая старая
//        EASY,   // Самая лёгкая
//        HARD    // Самая трудная
//    }
}
