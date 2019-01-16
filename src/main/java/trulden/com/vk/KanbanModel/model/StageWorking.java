package trulden.com.vk.KanbanModel.model;

import java.util.ArrayList;

public class StageWorking extends Stage {
    private ArrayList<Task> tasksInWork;
    private ArrayList<Task> finishedTasks;

    public StageWorking(StageType type, int WIPLimit) {
        super(type, WIPLimit);
        tasksInWork   = new ArrayList<>();
        finishedTasks = new ArrayList<>();
    }

    @Override
    public boolean canAddTask() {
        return getWIPLimit() > (tasksInWork.size() + finishedTasks.size());
    }

    @Override
    public void addTask(Task task) {
        if(task.getWorkAtStage(TYPE) == 0)
            finishedTasks.add(task);
        else
            tasksInWork.add(task);
    }

    public void moveTaskToFinished(Task task){
        tasksInWork.remove(task);
        finishedTasks.add(task);
    }

    @Override
    public void removeTask(Task task) {
        finishedTasks.remove(task);
    }

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

    @Override
    public int getNumberOfTasks() {
        return tasksInWork.size() + finishedTasks.size();
    }

    @Override
    public Task[] getTasksToRemove() {
        return finishedTasks.toArray(new Task[0]);
    }

    public Task[] getTasksInWork(){
        return tasksInWork.toArray(new Task[0]);
    }
}
