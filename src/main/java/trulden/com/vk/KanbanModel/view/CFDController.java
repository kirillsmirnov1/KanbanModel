package trulden.com.vk.KanbanModel.view;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import trulden.com.vk.KanbanModel.model.StageType;

import java.util.HashMap;

public class CFDController {

    private XYChart.Series[] CFDSeries;

    @FXML
    AreaChart CFDChart;

    public void setDayTracking(IntegerProperty currentDay, HashMap<Integer, int[]> CFD) {
        int numberOfSeries = CFD.get(0).length;

        CFDSeries = new XYChart.Series[numberOfSeries];

        for(int i=0; i < numberOfSeries; ++i){
            CFDSeries[i] = new XYChart.Series();

            if(i < numberOfSeries - 1)
                CFDSeries[i].setName(StageType.values()[i].toString());
            else
                CFDSeries[i].setName("DEPLOYED");
            
            CFDChart.getData().addAll(CFDSeries[i]);
        }

        // Начальное заполнение столбцов
        for(int day=0; day < CFD.size(); ++day){
            for(int seriesIterator = 0; seriesIterator < numberOfSeries; ++seriesIterator){
                CFDSeries[seriesIterator].getData().add(new XYChart.Data(day, CFD.get(day)[seriesIterator]));
            }
        }

        // Установка слежки за изменением дня
        currentDay.addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
            for(int seriesIterator=0; seriesIterator < numberOfSeries; ++seriesIterator){
                CFDSeries[seriesIterator].getData().add(new XYChart.Data(oldValue, CFD.get(oldValue)[seriesIterator]));
            }
        }));
    }
}
