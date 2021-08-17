package ru.mm.surv.capture.service.impl;

import java.util.function.Consumer;

class LivenessChecker implements Runnable {

    private final Process process;
    private final Consumer<Process> action;

    public LivenessChecker(Process process, Consumer<Process> action) {
        this.process = process;
        this.action = action;
    }

    @Override
    public void run() {
        if (!process.isAlive()) {
            action.accept(process);
        }
    }
}
