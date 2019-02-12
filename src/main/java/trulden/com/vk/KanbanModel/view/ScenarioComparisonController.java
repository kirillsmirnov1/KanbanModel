package trulden.com.vk.KanbanModel.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import trulden.com.vk.KanbanModel.model.ResultOfModel;

public class ScenarioComparisonController {

    private XYChart.Series leadTimeSeries;
    private XYChart.Series cycleTimeSeries;
    private XYChart.Series tasksFinishedSeries;

    @FXML
    AreaChart scenariosChart;

    @FXML
    private void initialize(){
        leadTimeSeries = new XYChart.Series();
        cycleTimeSeries = new XYChart.Series();
        tasksFinishedSeries = new XYChart.Series();

        leadTimeSeries.setName("Lead time");
        cycleTimeSeries.setName("Cycle time");
        tasksFinishedSeries.setName("Завершено задач");

        scenariosChart.getData().addAll(leadTimeSeries, cycleTimeSeries, tasksFinishedSeries);
    }

    public void addResult(int pos, ResultOfModel result) {
        Platform.runLater(() -> {
            leadTimeSeries.getData().add(new XYChart.Data(pos, result.getLeadTime()));
            cycleTimeSeries.getData().add(new XYChart.Data(pos, result.getCycleTime()));
            tasksFinishedSeries.getData().add(new XYChart.Data(pos, result.getTasksFinished()));
        });
    }
}
