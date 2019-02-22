package trulden.com.vk.KanbanModel.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import trulden.com.vk.KanbanModel.MainApp;

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
    }

    @FXML
    private void onStartModelButtonPress(){
        mainApp.startModel();
    }

    @FXML
    private void onShowResults(){
        mainApp.showScenariosResults();
    }
}
