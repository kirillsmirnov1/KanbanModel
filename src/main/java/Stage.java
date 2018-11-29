import java.util.ArrayList;

// Абстрактный класс Стадии
public abstract class Stage {
    protected int WIPLimit;               // Ограничение на количество задач
    protected final WorkStages TYPE;      // Тип Стадии

    public int getWIPLimit() {
        return WIPLimit;
    }

    public void setWIPLimit(int WIPLimit) {
        this.WIPLimit = WIPLimit;
    }

    public WorkStages getTYPE() {
        return TYPE;
    }

    // Конструктор, заполняет тип и лимит Стадии
    Stage(WorkStages type, int WIPLimit){
        this.TYPE = type;
        this.WIPLimit = WIPLimit;
    }

    public abstract boolean canAddTask();
    public abstract void    addTask(Task task);
    public abstract void    removeTask(Task task);

    @Override
    public String toString(){
        return "S: " + TYPE.toString() + " wl: " + WIPLimit;
    }

    public abstract int freeSpace();

    public abstract void printTasks();
}
