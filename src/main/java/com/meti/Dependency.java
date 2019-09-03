package com.meti;

import java.nio.file.Path;

public interface Dependency {
    Path formatPath(Path parent);

    String getURLString();
}
