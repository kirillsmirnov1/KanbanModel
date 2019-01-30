package trulden.com.vk.KanbanModel.util;


public class Scenario {
    private boolean allRoundEmployees   = true;     // TODO
    private boolean linearTasksMovement = true;     // TODO
    private boolean workersNeedToRest   = false;    // TODO
    private boolean performCardsFromRightToLeft = true; // TODO

    private int deploymentFrequency = 7; // TODO

    private CardPriority cardPriority = CardPriority.OLD; // TODO

    // Зона геттеров и сеттеров

    public boolean isAllRoundEmployees() {
        return allRoundEmployees;
    }

    public void setAllRoundEmployees(boolean allRoundEmployees) {
        this.allRoundEmployees = allRoundEmployees;
    }

    public boolean isLinearTasksMovement() {
        return linearTasksMovement;
    }

    public void setLinearTasksMovement(boolean linearTasksMovement) {
        this.linearTasksMovement = linearTasksMovement;
    }

    public boolean isPerformCardsFromRightToLeft() {
        return performCardsFromRightToLeft;
    }

    public void setPerformCardsFromRightToLeft(boolean performCardsFromRightToLeft) {
        this.performCardsFromRightToLeft = performCardsFromRightToLeft;
    }

    public CardPriority getCardPriority() {
        return cardPriority;
    }

    public void setCardPriority(CardPriority cardPriority) {
        this.cardPriority = cardPriority;
    }

    public int getDeploymentFrequency() {
        return deploymentFrequency;
    }

    public void setDeploymentFrequency(int deploymentFrequency) {
        this.deploymentFrequency = deploymentFrequency;
    }

    public boolean isWorkersNeedToRest() {
        return workersNeedToRest;
    }

    public void setWorkersNeedToRest(boolean workersNeedToRest) {
        this.workersNeedToRest = workersNeedToRest;
    }

    // Приоритет выбора карточки для выполнения

    public enum  CardPriority {
        OLD,    // Самая старая
        EASY,   // Самая лёгкая
        HARD    // Самая трудная
    }
}
