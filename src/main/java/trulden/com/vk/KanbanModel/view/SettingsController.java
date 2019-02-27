package trulden.com.vk.KanbanModel.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import trulden.com.vk.KanbanModel.MainApp;
import trulden.com.vk.KanbanModel.model.Model;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsController {

    @FXML
    TextField numberOfDaysTextField;
    @FXML
    TextField numberOfWorkersTextField;
    @FXML
    TextField UISleepTimeTextField;
    @FXML
    TextField scenariosPathTextField;
    @FXML
    CheckBox showBoardCheckBox;
    @FXML
    Button startModelButton;
    @FXML
    Button resultsGraphicButton; // FIXME замьютить до запуска модели

    MainApp mainApp;

    // Заполнение полей формы и сохранение ссылки на основное приложение
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        numberOfDaysTextField.setText(Integer.toString(Model.getNumberOfDays()));
        numberOfWorkersTextField.setText(Integer.toString(Model.getNumberOfWorkers()));
        UISleepTimeTextField.setText(Integer.toString(Model.getTimeToSleep()));
        scenariosPathTextField.setText(mainApp.getScenariosPathAsString());
        showBoardCheckBox.setSelected(mainApp.showingKanbanBoard());
    }

    // По клику чекбокса отображения доски − меняется отображение доски
    @FXML
    private void onCheckBoxChange(){
        mainApp.setShowBoard(showBoardCheckBox.isSelected());
    }

    // Проверка на корректность введеных целочисленных значений
    private boolean checkIntegerTextField(TextField tf, int minimum){
        try {
            if(Integer.parseInt(tf.getText()) < minimum){
                tf.appendText(" Error!");
                return false;
            }
        } catch (NumberFormatException e){
            tf.appendText(" Error!");
            return false;
        }
        return true;
    }

    // Проверка пути к сценариям на корректность
    private boolean checkScenariosValue() {
        String str = scenariosPathTextField.getText();

        if(Files.exists(Paths.get(str)))
            return true;
        else{
            scenariosPathTextField.appendText(" Error!");
            return false;
        }
    }

    // Показать таблицу с результатами прогона сценариев
    @FXML
    private void onShowResults(){
        mainApp.showScenariosResults();
    }

    // Запуск сценариев модели
    @FXML
    private void onStartModelButtonPress(){
        if( ! ( checkIntegerTextField(numberOfDaysTextField, 1)
                && checkIntegerTextField(numberOfWorkersTextField, 1)
                && checkIntegerTextField(UISleepTimeTextField, showBoardCheckBox.isSelected() ? 5 : 0)
                && checkScenariosValue()))
            return;

        Model.setNumberOfDays(Integer.parseInt(numberOfDaysTextField.getText()));
        Model.setNumberOfWorkers(Integer.parseInt(numberOfWorkersTextField.getText()));
        Model.setTimeToSleep(Integer.parseInt(UISleepTimeTextField.getText()));
        mainApp.setScenariosPath(scenariosPathTextField.getText());

        mainApp.startModel();
    }
}
