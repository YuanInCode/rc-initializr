package redcoder.rcinitializr.projectcontributor;

import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import redcoder.rcinitializr.support.ReplaceProjectContributorConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * 删除文件HELP.md
 *
 * @author redcoder54
 * @since 2021-08-16
 * @deprecated 使用 {@link ReplaceProjectContributorConfiguration} 替代文件删除
 */
@Deprecated
class RemoveHelpDocProjectContributor implements ProjectContributor {

    private static final Logger log = LoggerFactory.getLogger(RemoveMavenWrapperProjectContributor.class);
    private static final String FILENAME = "HELP.md";

    @Override
    public void contribute(Path projectRoot) throws IOException {
        File helpDocFile = getHelpDocFile(projectRoot.toFile());
        if (helpDocFile != null && helpDocFile.delete()) {
            log.debug("delete file 'HELP.md' successfully");
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Nullable
    private File getHelpDocFile(File file) {
        if (file.isFile() && FILENAME.equals(file.getName())) {
            return file;
        }
        File[] subFiles = file.listFiles();
        if (subFiles != null) {
            for (File f : subFiles) {
                File target = getHelpDocFile(f);
                if (target != null) {
                    return target;
                }
            }
        }
        return null;
    }
}
