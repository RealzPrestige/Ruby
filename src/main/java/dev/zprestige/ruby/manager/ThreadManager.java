package dev.zprestige.ruby.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void run(Runnable command) {
        executorService.execute(command);
    }
}