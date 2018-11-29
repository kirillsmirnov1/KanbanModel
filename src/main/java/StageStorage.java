import java.util.ArrayList;

public class StageStorage extends Stage {
    private ArrayList<Task> tasksInStorage;

    StageStorage(WorkStages type, int WIPLimit){
        super(type, WIPLimit);
        tasksInStorage = new ArrayList<>();
    }

    @Override
    public boolean canAddTask(){
        return WIPLimit > tasksInStorage.size();
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
    public int freeSpace() {
        return WIPLimit - tasksInStorage.size();
    }

    @Override
    public void printTasks(){
        tasksInStorage.forEach(System.out::println);
    }
}
