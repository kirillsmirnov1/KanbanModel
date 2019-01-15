package trulden.com.vk.KanbanModel.model;

// Абстрактный класс Стадии
public abstract class Stage {
    private int WIPLimit;               // Ограничение на количество задач

    protected final StageType TYPE;      // Тип Стадии
    // Конструктор, заполняет тип и лимит Стадии

    public Stage(StageType type, int WIPLimit){
        this.TYPE = type;
        this.WIPLimit = WIPLimit;
    }

    public int getWIPLimit() {
        return WIPLimit;
    }

    @Override
    public String toString(){
        return "S: " + TYPE.toString() + " wl: " + WIPLimit;
    }

    public abstract boolean canAddTask();
    public abstract void    addTask(Task task);
    public abstract void    removeTask(Task task);
    public abstract String  composeTasksStatus();
    public abstract int     getNumberOfTasks();
    public abstract Task[]  getTasksToRemove();
}
