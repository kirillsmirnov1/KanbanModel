package trulden.com.vk.KanbanModel.model;

// Результаты выполнения сценария
public class ScenarioResults {
    // Время тасок на доске
    private final double leadTime;
    // Время тасок на рабочих стадиях
    private final double cycleTime;
    // Количество завершенных задач
    private final int    tasksFinished;

    ScenarioResults(double leadTime, double cycleTime, int tasksFinished){
        this.leadTime = leadTime;
        this.cycleTime = cycleTime;
        this.tasksFinished = tasksFinished;
    }

    public double getLeadTime() {
        return leadTime;
    }

    public double getCycleTime() {
        return cycleTime;
    }

    public int getTasksFinished() {
        return tasksFinished;
    }
}
