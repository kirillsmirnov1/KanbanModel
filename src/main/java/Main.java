import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

// TODO графооооон
// TODO статистика и графики

public class Main extends Application{

    static public String[] workerNames = new String[Model.getNumberOfWorkers()];
    Model model;

    public static void main(String[] args) {
        fillWorkerNames();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("Kanban Model");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        model = new Model();// TODO добавить в модель функции-итераторы вида "сделай следующий шаг"
        model.start();
    }

    static void fillWorkerNames(){ // TODO отобрать имена, чтобы не было слишком длинных
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
        } catch (IOException e){ // TODO нормальный отлов исключений
            e.printStackTrace();
        }
    }
}
