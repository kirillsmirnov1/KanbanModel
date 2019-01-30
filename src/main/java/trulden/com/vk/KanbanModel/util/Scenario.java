package trulden.com.vk.KanbanModel.util;


public class Scenario {
    //private boolean allRoundEmployees   = true;     // TODO
    private boolean linearTasksMovement = true;     // TODO
    //private boolean workersNeedToRest   = false;    // TODO
    //private boolean performCardsFromRightToLeft = true; // TODO

    //private CardPriority cardPriority = CardPriority.OLD; // TODO

    private int deploymentFrequency = 7; // TODO

    // Зона геттеров и сеттеров

    public boolean isLinearTasksMovement() {
        return linearTasksMovement;
    }

    public void setLinearTasksMovement(boolean linearTasksMovement) {
        this.linearTasksMovement = linearTasksMovement;
    }

    public int getDeploymentFrequency() {
        return deploymentFrequency;
    }

    public void setDeploymentFrequency(int deploymentFrequency) {
        this.deploymentFrequency = deploymentFrequency;
    }

    // Приоритет выбора карточки для выполнения

//    public enum  CardPriority {
//        OLD,    // Самая старая
//        EASY,   // Самая лёгкая
//        HARD    // Самая трудная
//    }
}
