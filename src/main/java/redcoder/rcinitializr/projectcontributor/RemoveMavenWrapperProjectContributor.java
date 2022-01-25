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
 * 删除maven wrapper文件和脚本文件
 *
 * @author redcoder54
 * @since 2021-08-16
 * @deprecated 使用 {@link ReplaceProjectContributorConfiguration} 替代文件删除
 */
@Deprecated
public class RemoveMavenWrapperProjectContributor implements ProjectContributor {

    private static final Logger log = LoggerFactory.getLogger(RemoveMavenWrapperProjectContributor.class);

    private static final String MVN_SCRIPT_FOR_LINUX = "mvnw";
    private static final String MVN_SCRIPT_FOR_WINDOWS = "mvnw.cmd";
    private static final String DOT_MVN_DIR = ".mvn";

    @Override
    public void contribute(Path projectRoot) throws IOException {
        File rootFile = projectRoot.toFile();
        // delete file mvnw
        File targetFile = FileUtils.getFile(rootFile, MVN_SCRIPT_FOR_LINUX);
        if (targetFile != null && targetFile.delete()) {
            log.debug("delete file 'mvnw' successfully");
        }
        // delete file mvnw.cmd
        targetFile = FileUtils.getFile(rootFile, MVN_SCRIPT_FOR_WINDOWS);
        if (targetFile != null && targetFile.delete()) {
            log.debug("delete file 'mvnw.cmd' successfully");
        }
        // delete directory .mvn
        targetFile = FileUtils.getDirFile(rootFile, DOT_MVN_DIR);
        if (targetFile != null && FileUtils.deleteDir(targetFile)) {
            log.debug("delete directory '.mvn' successfully");
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
