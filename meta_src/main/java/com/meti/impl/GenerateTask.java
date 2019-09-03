package com.meti.impl;

import com.meti.task.Task;

import java.util.Map;

public class GenerateTask extends EnumeratedTask {
    @Override
    protected Class<GenerateType> getEnumClass() {
        return GenerateType.class;
    }

    @Override
    protected void putTasks(Map<GenerateType, Task> tasks) {
        tasks.put(GenerateType.SOURCE, new SourceTask());
        tasks.put(GenerateType.COMPILE, new OutTask());
    }

    @Override
    public String getName() {
        return "generate";
    }
}