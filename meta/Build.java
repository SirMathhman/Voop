import com.meti.Dependency;
import com.meti.MavenDependency;
import com.meti.State;
import com.meti.impl.Script;

import java.util.Set;

public class Build implements Script {
    @Override
    public void build(State state) {
        Set<Dependency> dependencies = state.getDependencies();
        dependencies.add(new MavenDependency("org.junit.jupiter", "junit-jupiter-api", "5.5.1"));
        dependencies.add(new MavenDependency("org.junit.jupiter", "junit-jupiter-engine", "5.5.1"));
    }
}