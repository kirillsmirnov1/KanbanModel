package trulden.com.vk.KanbanModel.view;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

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
            CFDSeries[i].setName(Integer.toString(i));
            // TODO задать нормальные имена сериям
            CFDChart.getData().addAll(CFDSeries[i]);
        }

        for(int day=0; day < CFD.size(); ++day){
            int[] CFDForToday = CFD.get(day);
            for(int seriesIterator = 0; seriesIterator < numberOfSeries; ++seriesIterator){
                CFDSeries[seriesIterator].getData().add(new XYChart.Data(day, CFDForToday[seriesIterator]));
            }
        }
    }
}
