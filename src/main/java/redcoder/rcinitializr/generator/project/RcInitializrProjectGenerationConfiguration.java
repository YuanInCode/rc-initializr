package redcoder.rcinitializr.generator.project;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.springframework.context.annotation.Bean;
import redcoder.rcinitializr.generator.project.contributor.*;

/**
 * 配置自定义的{@link ProjectContributor}
 */
@ProjectGenerationConfiguration
public class RcInitializrProjectGenerationConfiguration {

    @Bean
    public TemplateSourceCodeProjectContributor fileSourceCodeProjectContributor(ProjectDescription projectDescription) {
        return new TemplateSourceCodeProjectContributor(projectDescription);
    }

    @Bean
    public TemplateConfigFileProjectContributor templateConfigFileProjectContributor(ProjectDescription projectDescription) {
        return new TemplateConfigFileProjectContributor(projectDescription);
    }

    @Bean
    public BuildScriptProjectContributor buildScriptProjectContributor() {
        return new BuildScriptProjectContributor();
    }

    @Bean
    public PrettyMavenBuildProjectContributor prettyMavenBuildProjectContributor(MavenBuild build,
                                                                                 IndentingWriterFactory indentingWriterFactory) {
        return new PrettyMavenBuildProjectContributor(build, indentingWriterFactory);
    }

    @Bean
    public ReplacerProjectContributorProcessor replacerProjectContributorProcessor() {
        return new ReplacerProjectContributorProcessor();
    }
}
