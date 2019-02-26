package trulden.com.vk.KanbanModel.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import trulden.com.vk.KanbanModel.MainApp;
import trulden.com.vk.KanbanModel.model.Model;

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
    Button resultsGraphicButton;

    MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        showBoardCheckBox.setSelected(mainApp.getShowBoard());
        numberOfDaysTextField.setText(Integer.toString(Model.getNumberOfDays()));
        numberOfWorkersTextField.setText(Integer.toString(Model.getNumberOfWorkers()));
        UISleepTimeTextField.setText(Integer.toString(Model.getTimeToSleep()));
        scenariosPathTextField.setText(mainApp.getScenariosPathAsString());
    }

    @FXML
    private void onStartModelButtonPress(){
        mainApp.startModel();
    }

    @FXML
    private void onShowResults(){
        mainApp.showScenariosResults();
    }

    @FXML
    private void onCheckBoxChange(){
        mainApp.setShowBoard(showBoardCheckBox.isSelected());
    }
}
