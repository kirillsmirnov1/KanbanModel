package trulden.com.vk.KanbanModel.model;

import org.junit.Rule;
import org.junit.jupiter.api.*;

import static org.junit.Assert.assertEquals;

class WorkerTest {

    static Worker worker;

    @BeforeAll
    static void initWorker(){
        worker = Worker.generateRandomWorker("worker");
    }

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