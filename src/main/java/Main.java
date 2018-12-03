import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Main {

    static public String[] workerNames = new String[Model.getNumberOfWorkers()];

    public static void main(String[] args) {
        fillWorkerNames();

        Model model = new Model();
        model.start();
    }

    static void fillWorkerNames(){ // TODO отобрать имена, чтобы не было слишком длинных
        int[] lineNumbers;
        int numberOfLines;
        int lineCounter = 1;

        try {
            BufferedReader br = new BufferedReader(new FileReader("animals.txt"));
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
