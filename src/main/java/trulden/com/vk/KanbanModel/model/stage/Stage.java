package trulden.com.vk.KanbanModel.model.stage;

import trulden.com.vk.KanbanModel.model.Task;

// Абстрактный класс Стадии
public abstract class Stage {
    // Ограничение на количество задач
    private int WIPLimit;

    // Тип Стадии
    final StageType TYPE;

    // Конструктор, заполняет тип и лимит Стадии
    Stage(StageType type, int WIPLimit){
        this.TYPE = type;
        this.WIPLimit = WIPLimit;
    }

    @Override
    public String toString(){
        return "S: " + TYPE.toString() + " wl: " + WIPLimit;
    }

    public int getWIPLimit() {
        return WIPLimit;
    }

    // Можо ли добавить задачу на доску?
    public abstract boolean canAddTask();
    // Добавление задачи на доску
    public abstract void    addTask(Task task);
    // Удалить задачу с доски
    public abstract void    removeTask(Task task);
    // Выдает статус задач на доске в виде строки
    public abstract String  composeTasksStatus();
    // Завершенные таски, которые можно двигать дальше
    public abstract Task[]  getTasksToRemove();
    // Количество задач на доске
    public abstract int     getNumberOfTasks();
    // Очистка доски
    public abstract void clean();
}
