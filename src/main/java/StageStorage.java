import java.util.ArrayList;

public class StageStorage extends Stage {
    private ArrayList<Task> tasksInStorage;

    StageStorage(StageType type, int WIPLimit){
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
    public void printTasks(){
        if(tasksInStorage.size() > 0) {
            System.out.println("In storage: ");
            tasksInStorage.forEach(System.out::println);
        }
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
