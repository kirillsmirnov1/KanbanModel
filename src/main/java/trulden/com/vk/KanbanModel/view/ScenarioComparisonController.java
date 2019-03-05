package trulden.com.vk.KanbanModel.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import trulden.com.vk.KanbanModel.model.ScenarioResults;

public class ScenarioComparisonController {

    private XYChart.Series leadTimeSeries;
    private XYChart.Series cycleTimeSeries;
    private XYChart.Series tasksFinishedSeries;

    @FXML
    private CheckBox leadTimeCheckbox;
    @FXML
    private CheckBox cycleTimeCheckbox;
    @FXML
    private CheckBox tasksFinishedCheckbox;

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

    public void addResult(int pos, ScenarioResults result) {
        Platform.runLater(() -> {
            leadTimeSeries.getData().add(new XYChart.Data(pos, result.getLeadTime()));
            cycleTimeSeries.getData().add(new XYChart.Data(pos, result.getCycleTime()));
            tasksFinishedSeries.getData().add(new XYChart.Data(pos, result.getTasksFinished()));
        });
    }

    @FXML
    private void onLeadTimeCheckboxAction(){
        if(leadTimeCheckbox.isSelected()){
            scenariosChart.getData().add(leadTimeSeries);
        } else {
            scenariosChart.getData().remove(leadTimeSeries);
        }
    }

    @FXML
    private void onCycleTimeCheckboxAction(){
        if(cycleTimeCheckbox.isSelected()){
            scenariosChart.getData().add(cycleTimeSeries);
        } else {
            scenariosChart.getData().remove(cycleTimeSeries);
        }
    }

    @FXML
    private void onTasksFinishedCheckboxAction(){
        if(tasksFinishedCheckbox.isSelected()){
            scenariosChart.getData().add(tasksFinishedSeries);
        } else {
            scenariosChart.getData().remove(tasksFinishedSeries);
        }
    }
}
