package trulden.com.vk.KanbanModel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import trulden.com.vk.KanbanModel.model.Model;
import trulden.com.vk.KanbanModel.view.MainWindowController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class MainApp extends Application{

    static public String[] workerNames = new String[Model.getNumberOfWorkers()];
    Model model;
    public MainWindowController mainWindowController;

    public static void main(String[] args) {
        fillWorkerNames();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        URL url = getClass().getResource("/trulden/com/vk/KanbanModel/view/MainWindow.fxml");
        loader.setLocation(url);
        Parent root = loader.load();
        primaryStage.setTitle("Kanban Model");
        primaryStage.setScene(new Scene(root, 1300, 400));
        primaryStage.setResizable(false);

        mainWindowController = loader.getController();

        primaryStage.show();

        new Thread(new Model(mainWindowController)).start();
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
}
