package redcoder.rcinitializr.generator.project.contributor;

import io.spring.initializr.generator.project.contributor.ProjectContributor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * ProjectContributor的空实现，什么也不做
 */
public class NoOpProjectContributor implements ProjectContributor {

    @Override
    public void contribute(Path projectRoot) throws IOException {
        // no operation
    }
}
