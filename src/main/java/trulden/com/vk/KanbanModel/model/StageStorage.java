package trulden.com.vk.KanbanModel.model;

import java.util.ArrayList;

public class StageStorage extends Stage {
    private ArrayList<Task> tasksInStorage;

    public StageStorage(StageType type, int WIPLimit){
        super(type, WIPLimit);
        tasksInStorage = new ArrayList<>();
    }

    @Override
    public boolean canAddTask(){
        return getWIPLimit() > tasksInStorage.size();
    }

    @Override
    public void addTask(Task task){
        tasksInStorage.add(task);
    }

    @Override
    public void removeTask(Task task){
        tasksInStorage.remove(task);
    }

    @Override
    public String composeTasksStatus(){
        StringBuilder str = new StringBuilder();
        if(tasksInStorage.size()>0){
            str.append("In storage:").append("\n");
            tasksInStorage.forEach(t -> str.append(t.toString()).append("\n"));
        }
        return str.toString();
    }

    @Override
    public int getNumberOfTasks() {
        return tasksInStorage.size();
    }

    @Override
    public Task[] getTasksToRemove() {
        return tasksInStorage.toArray(new Task[0]);
    }
}
