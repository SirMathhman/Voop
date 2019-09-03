package com.meti;

import java.nio.file.Path;

public class MavenDependency implements Dependency {
    private final String artifactID;
    private final String groupID;
    private final String version;

    public MavenDependency(String groupID, String artifactID, String version) {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
    }

    @Override
    public Path formatPath(Path parent) {
        return parent.resolve(groupID + "&" + artifactID + "&" + version + ".jar");
    }

    @Override
    public String getURLString() {
        return "https://repo1.maven.org/maven2/" + this.groupID.replace(".", "/") + "/" + artifactID + "/" + version + "/" + artifactID + "-" + version + ".jar";
    }
}