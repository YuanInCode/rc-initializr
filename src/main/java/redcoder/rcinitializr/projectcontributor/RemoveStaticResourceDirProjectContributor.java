package redcoder.rcinitializr.projectcontributor;

import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redcoder.rcinitializr.support.ReplaceProjectContributorConfiguration;
import redcoder.rcinitializr.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * 删除 src/main/resources 下的两个目录：templates和static
 *
 * @author redcoder54
 * @since 2021-08-16
 * @deprecated 使用 {@link ReplaceProjectContributorConfiguration} 替代文件删除
 */
@Deprecated
public class RemoveStaticResourceDirProjectContributor implements ProjectContributor {

    private static final Logger log = LoggerFactory.getLogger(RemoveStaticResourceDirProjectContributor.class);

    private static final String TEMPLATES_DIR = "templates";
    private static final String STATIC_DIR = "static";

    @Override
    public void contribute(Path projectRoot) throws IOException {
        File rootFile = projectRoot.toFile();
        // delete directory templates
        File targetFile = FileUtils.getDirFile(rootFile, TEMPLATES_DIR);
        if (targetFile != null && FileUtils.deleteDir(targetFile)) {
            log.debug("delete directory 'templates' successfully");
        }
        // delete directory static
        targetFile = FileUtils.getDirFile(rootFile, STATIC_DIR);
        if (targetFile != null && FileUtils.deleteDir(targetFile)) {
            log.debug("delete directory 'static' successfully");
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
