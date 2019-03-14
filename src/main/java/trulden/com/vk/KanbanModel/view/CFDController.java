package trulden.com.vk.KanbanModel.view;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import trulden.com.vk.KanbanModel.model.Model;
import trulden.com.vk.KanbanModel.model.stage.StageType;

import java.util.HashMap;

public class CFDController {

    private XYChart.Series[] CFDSeries;

    @FXML
    AreaChart CFDChart;
    @FXML
    NumberAxis daysAxis;
    @FXML
    NumberAxis tasksAxis;

    @FXML
    private void initialize(){
        daysAxis.setUpperBound(Model.getNumberOfDays());
        daysAxis.setTickUnit(Model.getNumberOfDays()/10);

        tasksAxis.setUpperBound(10);
    }

    public void setDayTracking(IntegerProperty currentDay, HashMap<Integer, int[]> CFD) {
        int numberOfSeries = StageType.values().length + 1;

        CFDSeries = new XYChart.Series[numberOfSeries];

        for(int i=0; i < numberOfSeries; ++i){
            CFDSeries[i] = new XYChart.Series();

            if(i < numberOfSeries - 1)
                CFDSeries[i].setName(StageType.values()[i].toString());
            else
                CFDSeries[i].setName("Поставлено");

            CFDChart.getData().addAll(CFDSeries[i]);
        }

        // Начальное заполнение столбцов
        for(int day=0; day < CFD.size(); ++day){

            // Шкала задач кратна десяти
            if(CFD.get(CFD.size()-1)[0] >= tasksAxis.getUpperBound()){
                tasksAxis.setUpperBound(CFD.get(CFD.size()-1)[0] - CFD.get(CFD.size()-1)[numberOfSeries-1]%10);
            }

            for(int seriesIterator = 0; seriesIterator < numberOfSeries; ++seriesIterator){
                CFDSeries[seriesIterator].getData().add(new XYChart.Data(day+1, CFD.get(day)[seriesIterator]));
            }
        }

        // Установка слежки за изменением дня
        currentDay.addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {

            // Поддержание кратности шкалы
            if(CFD.get(oldValue)[0] >= tasksAxis.getUpperBound()){
                tasksAxis.setUpperBound(tasksAxis.getUpperBound()+10);
            }

            for(int seriesIterator=0; seriesIterator < numberOfSeries; ++seriesIterator){
                CFDSeries[seriesIterator].getData().add(new XYChart.Data(oldValue.intValue()+1, CFD.get(oldValue)[seriesIterator]));
            }
        }));
    }

    public void clear() {
        CFDChart.getData().clear();
    }
}
