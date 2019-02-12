package trulden.com.vk.KanbanModel.model;

public class ResultOfModel {
    private final double leadTime;
    private final double cycleTime;
    private final int    tasksFinished;

    public double getLeadTime() {
        return leadTime;
    }

    public double getCycleTime() {
        return cycleTime;
    }

    public int getTasksFinished() {
        return tasksFinished;
    }

    ResultOfModel(double leadTime, double cycleTime, int tasksFinished){
        this.leadTime = leadTime;
        this.cycleTime = cycleTime;
        this.tasksFinished = tasksFinished;
    }
}
