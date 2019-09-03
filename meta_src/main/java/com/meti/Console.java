package com.meti;

import com.meti.impl.*;
import com.meti.task.SetTaskManager;
import com.meti.task.TaskManager;
import com.meti.task.TaskResponse;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

class Console {
    private final Logger logger = Logger.getAnonymousLogger();
    private final TaskManager manager = new SetTaskManager();
    private final State state = new SimpleState(manager);

    public static void main(String[] args) {
        new Console().run();
    }

    private void run() {
        init();
        waitForTermination();
    }

    private void init() {
        manager.add(new GenerateTask());
        manager.add(new CleanTask());
        manager.add(new CompileTask());
        manager.add(new RunTask());
        manager.add(new ExitTask());
    }

    private void waitForTermination() {
        Scanner scanner = new Scanner(System.in);
        boolean shouldTerminate;
        do {
            var line = scanner.nextLine();
            logger.log(Level.INFO, "Running task: " + line);
            shouldTerminate = evaluate(line);
        } while (!shouldTerminate);
    }

    private boolean evaluate(String line) {
        try {
            return evaluateExceptionally(line);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to execute task: " + line, e);
            return false;
        }
    }

    private boolean evaluateExceptionally(String line) throws Exception {
        var result = manager.run(state, line);
        if (result.isEmpty()) {
            logger.log(Level.WARNING, "Could not parse: " + line);
            return false;
        } else {
            return evaluate(result.get());
        }
    }

    private boolean evaluate(CompletableFuture<TaskResponse> result) throws InterruptedException,
            ExecutionException, TimeoutException {
        TaskResponse response = result.get(1, TimeUnit.SECONDS);
        logger.log(Level.INFO, response.getMessage());
        return response.shouldTerminate();
    }
}