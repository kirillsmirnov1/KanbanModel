// Абстрактный класс Стадии
public abstract class Stage {
    protected int WIPLimit;               // Ограничение на количество задач
    protected final StageType TYPE;      // Тип Стадии

    public int getWIPLimit() {
        return WIPLimit;
    }

    public void setWIPLimit(int WIPLimit) {
        this.WIPLimit = WIPLimit;
    }

    public StageType getTYPE() {
        return TYPE;
    }

    // Конструктор, заполняет тип и лимит Стадии
    Stage(StageType type, int WIPLimit){
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
