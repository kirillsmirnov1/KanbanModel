package trulden.com.vk.KanbanModel.model;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class WorkerTest {

    @Test()
    void testCorrectName(){
        String name = "name";
        assertEquals(name, Worker.generateRandomWorker(name).getName());
    }

    @Test()
    void emptyWorkerNameShouldThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                    Worker.generateRandomWorker("");
                }
        );
    }
}