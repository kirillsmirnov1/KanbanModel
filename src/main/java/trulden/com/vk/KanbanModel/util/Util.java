package trulden.com.vk.KanbanModel.util;

import java.util.concurrent.TimeUnit;

// Вспомогательные функции
public class Util {

    // Задержать выполнение потока на определенное время
    public static void sleepMilliseconds(int millisecondsToSleep){
        try{
            TimeUnit.MILLISECONDS.sleep(millisecondsToSleep);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    // Проверяет, запущена ли программа из jar-файла
    public static boolean runningFromJar() {
        return Util.class.getResource("/" + Util.class.getName().replace('.', '/') + ".class").toString().startsWith("jar:");
    }
}
