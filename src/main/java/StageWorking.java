import java.util.ArrayList;

public class StageWorking extends Stage {
    private ArrayList<Task> tasksInWork;
    private ArrayList<Task> finishedTasks;

    StageWorking(StageType type, int WIPLimit) {
        super(type, WIPLimit);
        tasksInWork   = new ArrayList<>();
        finishedTasks = new ArrayList<>();
    }

    @Override
    public boolean canAddTask() {
        return WIPLimit > (tasksInWork.size() + finishedTasks.size());
    }

    @Override
    public void addTask(Task task) {
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
    public int freeSpace() {
        return WIPLimit - (tasksInWork.size() + finishedTasks.size());
    }

    @Override
    public void printTasks(){
        tasksInWork.forEach(System.out::println);
        finishedTasks.forEach(System.out::println);
    }
}
