package trulden.com.vk.KanbanModel.model;

// Результаты выполнения сценария
public class ScenarioResults {
    // Время тасок на доске
    private double leadTime;
    // Время тасок на рабочих стадиях
    private double cycleTime;
    // Количество завершенных задач
    private double tasksFinished;
    // Количество прогонов сценария
    private final int numberOfRuns;

    // Конструктор для однократного прогона
    ScenarioResults(double leadTime, double cycleTime, int tasksFinished){
        this.leadTime = leadTime;
        this.cycleTime = cycleTime;
        this.tasksFinished = tasksFinished;

        numberOfRuns = 1;
    }

    // Конструктор для многократного прогона
    ScenarioResults(int numberOfRuns){
        this.numberOfRuns = numberOfRuns;

        leadTime = 0d;
        cycleTime = 0d;
        tasksFinished = 0d;
    }

    // Добавить результат одного прогона к общему усредненному результату
    public void addResult(double leadTime, double cycleTime, int tasksFinished){
        this.leadTime       += findFraction(leadTime);
        this.cycleTime      += findFraction(cycleTime);
        this.tasksFinished  += findFraction(tasksFinished);
    }

    // Нахожу пропорцию числа для добавления к усредненному
    private double findFraction(double number){
        return number / (1d * numberOfRuns);
    }

    public double getLeadTime() {
        return leadTime;
    }

    public double getCycleTime() {
        return cycleTime;
    }

    public double getTasksFinished() {
        return tasksFinished;
    }
}
