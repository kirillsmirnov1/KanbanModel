package trulden.com.vk.KanbanModel.model.stage;

import trulden.com.vk.KanbanModel.model.Task;

import java.util.ArrayList;

// Стадия хранения
public class StageStorage extends Stage {
    // Хранимые задачи
    private ArrayList<Task> tasksInStorage;

    public StageStorage(StageType type, int WIPLimit){
        super(type, WIPLimit);
        tasksInStorage = new ArrayList<>();
    }

    // Можно ли добавить задачу на доску
    @Override
    public boolean canAddTask(){
        return getWIPLimit() > tasksInStorage.size();
    }

    // Добавление таски на доску
    @Override
    public void addTask(Task task){
        tasksInStorage.add(task);
    }

    // Удаление таски с доски
    @Override
    public void removeTask(Task task){
        tasksInStorage.remove(task);
    }

    // Возвращает строкой статус задач на доске
    @Override
    public String composeTasksStatus(){
        StringBuilder str = new StringBuilder();
        if(tasksInStorage.size()>0){
            str.append("In storage:").append("\n");
            tasksInStorage.forEach(t -> str.append(t.toString()).append("\n"));
        }
        return str.toString();
    }

    // Возвращает задачи, которые можно двигать дальше по доске
    @Override
    public Task[] getTasksToRemove() {
        return tasksInStorage.toArray(new Task[0]);
    }

    // Очистка доски
    @Override
    public void clean() {
        tasksInStorage = new ArrayList<>();
    }

    @Override
    public int getNumberOfTasks() {
        return tasksInStorage.size();
    }
}
