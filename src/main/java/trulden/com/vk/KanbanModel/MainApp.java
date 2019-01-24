package trulden.com.vk.KanbanModel;

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
import com.google.gson.Gson;

public class MainApp extends Application{

    private int sceneW, sceneH;

    static public String[] workerNames;

    private Model model;
    private MainWindowController mainWindowController;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        parseJson();
        fillWorkerNames();

        FXMLLoader loader = new FXMLLoader();
        URL url = getClass().getResource("/trulden/com/vk/KanbanModel/view/MainWindow.fxml");
        loader.setLocation(url);
        Parent root = loader.load();
        primaryStage.setTitle("Kanban Model");
        primaryStage.setScene(new Scene(root, sceneW, sceneH));
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest( event -> System.exit(0));

        mainWindowController = loader.getController();

        primaryStage.show();
        model = new Model(mainWindowController);

        mainWindowController.setModel(model);
        new Thread(model).start();
    }

    private void parseJson() {
        try {
            JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("init.json"))));
            Model.setNumberOfDays(obj.getInt("NUMBER_OF_DAYS"));
            Model.setNumberOfWorkers(obj.getInt("NUMBER_OF_WORKERS"));
            Model.setDeploymentFrequency(obj.getInt("DEPLOYMENT_FREQUENCY"));
            Model.setTimeToSleep(obj.getInt("TIME_TO_SLEEP"));
            Model.setDefaultWip(new Gson().fromJson(obj.getString("DEFAULT_WIP"), int[].class));
            sceneW = obj.getInt("sceneW");
            sceneH = obj.getInt("sceneH");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fillWorkerNames(){
        int[] lineNumbers;
        int numberOfLines;
        int lineCounter = 1;

        workerNames = new String[Model.getNumberOfWorkers()];

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
}
