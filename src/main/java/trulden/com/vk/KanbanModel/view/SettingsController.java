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
    TextField numberOfRunsTextField;
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
    Button resultsGraphicButton;

    MainApp mainApp;

    // Заполнение полей формы и сохранение ссылки на основное приложение
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        numberOfRunsTextField.setText(Integer.toString(mainApp.getScenarioRuns()));
        numberOfDaysTextField.setText(Integer.toString(Model.getNumberOfDays()));
        numberOfWorkersTextField.setText(Integer.toString(Model.getNumberOfWorkers()));
        UISleepTimeTextField.setText(Integer.toString(Model.getUiRefreshDelay()));
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
        if( ! ( checkIntegerTextField(numberOfRunsTextField, 1)
                && checkIntegerTextField(numberOfDaysTextField, 1)
                && checkIntegerTextField(numberOfWorkersTextField, 1)
                && checkIntegerTextField(UISleepTimeTextField, showBoardCheckBox.isSelected() ? 5 : 0)
                && checkScenariosValue()))
            return;

        mainApp.setScenarioRuns(Integer.parseInt(numberOfRunsTextField.getText()));
        Model.setNumberOfDays(Integer.parseInt(numberOfDaysTextField.getText()));
        Model.setNumberOfWorkers(Integer.parseInt(numberOfWorkersTextField.getText()));
        Model.setUiRefreshDelay(Integer.parseInt(UISleepTimeTextField.getText()));
        mainApp.setScenariosPath(scenariosPathTextField.getText());

        resultsGraphicButton.setDisable(false);
        startModelButton.setDisable(true);
        showBoardCheckBox.setDisable(true);

        mainApp.startModel();
    }

    public void modelFinished() {
        startModelButton.setDisable(false);
        showBoardCheckBox.setDisable(false);
    }

    // Обновление времени задержки по нажатию соответствующей кнопки
    @FXML
    private void onRefreshUISleepTime(){
        if(! checkIntegerTextField(UISleepTimeTextField, showBoardCheckBox.isSelected() ? 5 : 0)) return;

        Model.setUiRefreshDelay(Integer.parseInt(UISleepTimeTextField.getText()));
    }
}
