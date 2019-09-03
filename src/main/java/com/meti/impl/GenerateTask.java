package com.meti.impl;

import com.meti.task.Task;

import java.util.Map;

import static com.meti.impl.GenerateType.*;
import static com.meti.impl.GenerateType.DEPENDENCIES;

public class GenerateTask extends EnumeratedTask {
    @Override
    protected Class<GenerateType> getEnumClass() {
        return GenerateType.class;
    }

    @Override
    protected void putTasks(Map<GenerateType, Task> tasks) {
        tasks.put(SOURCE, new SourceTask());
        tasks.put(COMPILE, new OutTask());
        tasks.put(META, new MetaTask());
        tasks.put(CONFIGURATION, new ConfigurationTask());
        tasks.put(DEPENDENCIES, new DependencyTask());
    }

    @Override
    public String getName() {
        return "generate";
    }
}