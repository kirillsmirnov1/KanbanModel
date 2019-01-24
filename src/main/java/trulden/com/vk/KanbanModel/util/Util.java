package trulden.com.vk.KanbanModel.util;

import java.util.concurrent.TimeUnit;

public class Util {
    public static void sleepMilliseconds(int millisecondsToSleep){
        try{
            TimeUnit.MILLISECONDS.sleep(millisecondsToSleep);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
