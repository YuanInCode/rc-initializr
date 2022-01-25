package redcoder.rcinitializr.projectcontributor;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.springframework.context.annotation.Bean;

/**
 * 配置自定义的{@link ProjectContributor}
 *
 * @author redcoder54
 * @since 2021-08-13
 */
@ProjectGenerationConfiguration
public class RcProjectContributorConfiguration {

    @Bean
    public TemplateSourceCodeProjectContributor fileSourceCodeProjectContributor(ProjectDescription projectDescription) {
        return new TemplateSourceCodeProjectContributor(projectDescription);
    }

    @Bean
    public TemplateConfigFileProjectContributor templateConfigFileProjectContributor(ProjectDescription projectDescription) {
        return new TemplateConfigFileProjectContributor(projectDescription);
    }
}
