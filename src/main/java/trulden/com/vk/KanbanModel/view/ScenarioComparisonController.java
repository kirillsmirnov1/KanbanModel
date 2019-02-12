package trulden.com.vk.KanbanModel.view;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;

public class ScenarioComparisonController {

    private XYChart.Series leadTimeSeries;
    private XYChart.Series cycleTimeSeries;
    private XYChart.Series tasksFinishedSeries;

    @FXML
    AreaChart scenariosChart;

    ScenarioComparisonController(){
        leadTimeSeries = new XYChart.Series();
        cycleTimeSeries = new XYChart.Series();
        tasksFinishedSeries = new XYChart.Series();

        scenariosChart.getData().addAll(leadTimeSeries, cycleTimeSeries, tasksFinishedSeries);
    }

}
