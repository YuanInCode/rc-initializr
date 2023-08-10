package redcoder.rcinitializr.configuration;

import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 配置{@link ReplaceProjectContributorBeanPostProcessor}
 *
 * @author redcoder54
 * @since 2021-09-22
 */
@ProjectGenerationConfiguration
public class ReplaceProjectContributorConfiguration {

    @Bean
    public ReplaceProjectContributorBeanPostProcessor replaceProjectContributorBeanPostProcessor() {
        return new ReplaceProjectContributorBeanPostProcessor();
    }

    /**
     * 将一些ProjectContributor实例替换为NoOpContributor实例：
     * <ul>
     *     <li>替换WebFoldersContributor，避免生成src/main/resources/templates和src/main/resources/static目录</li>
     *     <li>替换HelpDocumentProjectContributor，避免生成HELP.md文件</li>
     * </ul>
     */
    static class ReplaceProjectContributorBeanPostProcessor implements BeanPostProcessor {

        private final List<String> replaceList;
        private final ProjectContributor noOp;

        public ReplaceProjectContributorBeanPostProcessor() {
            replaceList = Collections.unmodifiableList(Arrays.asList("webFoldersContributor", "helpDocumentProjectContributor"));
            noOp = new NoOpProjectContributor();
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof ProjectContributor && replaceList.contains(beanName)) {
                return noOp;
            } else {
                return bean;
            }
        }
    }

    /**
     * ProjectContributor的空实现，什么也不做
     *
     * @author redcoder54
     * @since 2021-09-22
     */
    static class NoOpProjectContributor implements ProjectContributor {

        @Override
        public void contribute(Path projectRoot) throws IOException {
            // no operation
        }
    }
}
