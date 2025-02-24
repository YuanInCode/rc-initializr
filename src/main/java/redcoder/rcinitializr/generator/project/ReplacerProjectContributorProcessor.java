package redcoder.rcinitializr.generator.project;

import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * 将一些ProjectContributor实例替换为NoOpContributor实例：
 * <ul>
 *     <li>替换 WebFoldersContributor，不生成 src/main/resources/templates和src/main/resources/static目录</li>
 *     <li>替换 HelpDocumentProjectContributor，不生成HELP.md文件</li>
 *     <li>替换 mavenBuildProjectContributor，使用自定义的 MavenBuildProjectContributor</li>
 * </ul>
 */
public class ReplacerProjectContributorProcessor implements BeanPostProcessor {

     // asList("mavenWrapperContributor", "webFoldersContributor",
     //        "helpDocumentProjectContributor", "mavenBuildProjectContributor");

    private final List<String> replaceList;
    private final ProjectContributor noOp;

    public ReplacerProjectContributorProcessor() {
        replaceList = Collections.unmodifiableList(Arrays.asList("webFoldersContributor",
                "helpDocumentProjectContributor", "mavenBuildProjectContributor"));
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

    /**
     * ProjectContributor的空实现，什么也不做
     */
    static class NoOpProjectContributor implements ProjectContributor {

        @Override
        public void contribute(Path projectRoot) throws IOException {
            // no operation
        }
    }
}
