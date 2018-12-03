// Абстрактный класс Стадии
public abstract class Stage {
    protected int WIPLimit;               // Ограничение на количество задач
    protected final StageType TYPE;      // Тип Стадии

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

    public abstract void printTasks();
    
    public abstract int getNumberOfTasks();

    public abstract Task[] getTasksToRemove();
}
