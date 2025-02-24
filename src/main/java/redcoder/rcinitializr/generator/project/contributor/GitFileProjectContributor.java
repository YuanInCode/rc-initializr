package redcoder.rcinitializr.generator.project.contributor;

import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 添加.gitignore到项目根目录下
 */
public class GitFileProjectContributor implements ProjectContributor {

    private static final Logger log = LoggerFactory.getLogger(GitFileProjectContributor.class);

    // git文件所在目录
    private static final String rootResource = "classpath:template/git";
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();


    @Override
    public void contribute(Path projectRoot) throws IOException {
        Resource root = this.resolver.getResource(rootResource);
        Resource[] resources = this.resolver.getResources(rootResource + "/**");
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                String filename = resolveFilename(root.getURI(), resource.getURI());
                Path output = projectRoot.resolve(filename);
                Files.createDirectories(output.getParent());
                Files.createFile(output);
                FileCopyUtils.copy(resource.getInputStream(), Files.newOutputStream(output));
            }
        }
    }

    private String resolveFilename(URI root, URI resource) {
        String candidate = resource.toString().substring(root.toString().length());
        String filename = StringUtils.trimLeadingCharacter(candidate, '/');
        if (filename.equals("gitignore")) {
            filename = ".gitignore";
        }
        return filename;
    }
}
