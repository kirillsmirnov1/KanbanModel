package trulden.com.vk.KanbanModel.model.stage;

import trulden.com.vk.KanbanModel.model.Task;

import java.util.ArrayList;

// Рабочая стадия
public class StageWorking extends Stage {
    // Задачи в работе
    private ArrayList<Task> tasksInWork;
    // Завершенные задачи
    private ArrayList<Task> finishedTasks;

    public StageWorking(StageType type, int WIPLimit) {
        super(type, WIPLimit);
        tasksInWork   = new ArrayList<>();
        finishedTasks = new ArrayList<>();
    }

    // Проверяет, есть ли место на доске
    @Override
    public boolean canAddTask() {
        return getWIPLimit() > (tasksInWork.size() + finishedTasks.size());
    }

    // Добавляет таску на доску
    @Override
    public void addTask(Task task) {
        // Если работы на стадии нет, добавляется в завершенные
        if(task.getWorkAtStage(TYPE) == 0)
            finishedTasks.add(task);
        else
            tasksInWork.add(task);
    }

    // Переместить карточку из рабочих в завершенные
    public void moveTaskToFinished(Task task){
        tasksInWork.remove(task);
        finishedTasks.add(task);
    }

    // Удалить таску с доски
    @Override
    public void removeTask(Task task) {
        finishedTasks.remove(task);
    }

    // Составить строку статусов задач
    @Override
    public String composeTasksStatus(){
        StringBuilder str = new StringBuilder();
        if(tasksInWork.size()>0){
            str.append("In work:").append("\n");
            tasksInWork.forEach(t -> str.append(t.toString()).append("\n"));
        }
        if(finishedTasks.size()>0){
            str.append("Finished:").append("\n");
            finishedTasks.forEach(t -> str.append(t.toString()).append("\n"));
        }
        return str.toString();
    }

    // Задачи в работе
    public Task[] getTasksInWork(){
        return tasksInWork.toArray(new Task[0]);
    }

    // Завершенные задачи
    @Override
    public Task[] getTasksToRemove() {
        return finishedTasks.toArray(new Task[0]);
    }

    // Очистка доски
    @Override
    public void clean() {
        tasksInWork   = new ArrayList<>();
        finishedTasks = new ArrayList<>();
    }

    @Override
    public int getNumberOfTasks() {
        return tasksInWork.size() + finishedTasks.size();
    }
}
