package trulden.com.vk.KanbanModel;

import com.oracle.tools.packager.IOUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import trulden.com.vk.KanbanModel.model.Model;
import trulden.com.vk.KanbanModel.view.MainWindowController;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.json.*;

public class MainApp extends Application{

    private int sceneW = 1440, sceneH = 400;

    static public String[] workerNames = new String[Model.getNumberOfWorkers()];

    Model model;
    public MainWindowController mainWindowController;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        URL url = getClass().getResource("/trulden/com/vk/KanbanModel/view/MainWindow.fxml");
        loader.setLocation(url);
        Parent root = loader.load();
        primaryStage.setTitle("Kanban Model");
        primaryStage.setScene(new Scene(root, sceneW, sceneH));
        primaryStage.setResizable(false);

        
        parseJson();
        
        fillWorkerNames();

        mainWindowController = loader.getController();

        primaryStage.show();
        model = new Model(mainWindowController);
        new Thread(model).start();
    }

    private void parseJson() {
        try {
            JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("init.json"))));
            int d = obj.getInt("NUMBER_OF_DAYS");
        } catch (IOException e) {}
    }

    static void fillWorkerNames(){
        int[] lineNumbers;
        int numberOfLines;
        int lineCounter = 1;

        try {
            BufferedReader br = new BufferedReader(new FileReader("shortAnimals.txt"));
            String line = br.readLine();
            numberOfLines = Integer.parseInt(line);
            lineNumbers = new Random().ints(1, numberOfLines).limit(Model.getNumberOfWorkers()).sorted().toArray();

            for (int i = 0; i < Model.getNumberOfWorkers(); ++i) {
                while (lineCounter < lineNumbers[i] - 1) {
                    br.readLine();
                    lineCounter++;
                }
                workerNames[i] = br.readLine();
                lineCounter++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setSceneW(int sceneW) {
        this.sceneW = sceneW;
    }

    public void setSceneH(int sceneH) {
        this.sceneH = sceneH;
    }
}
