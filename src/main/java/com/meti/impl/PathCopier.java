package com.meti.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.*;

public class PathCopier implements Copier {
    private final Path from;
    private final Path to;

    PathCopier(Path from, Path to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void copy() throws IOException {
        copyRecursively(from, to);
    }

    private void copyChildren(Path from, Path to) throws IOException {
        Stream<Path> childStream = list(from);
        Set<Path> children = childStream.collect(Collectors.toSet());
        for (Path child : children) {
            copyRecursively(child, to.resolve(child.getFileName()));
        }
    }

    private void copyContent(Path from, Path to) throws IOException {
        if (exists(to)) delete(to);
        Files.copy(from, to);
    }

    private void copyRecursively(Path from, Path to) throws IOException {
        ensureParent(to);
        copyContent(from, to);
        if (isDirectory(from)) {
            copyChildren(from, to);
        }
    }

    private void ensureParent(Path to) throws IOException {
        Path parent = to.getParent();
        if (!exists(parent)) {
            createDirectories(parent);
        }
    }
}
