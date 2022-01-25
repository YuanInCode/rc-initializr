package redcoder.rcinitializr.projectcontributor;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 将resources/template/sourcecde目录下的文件添加项目的根包{@link ProjectDescription#getPackageName()}下，
 * java文件中的 ${package} 会被替换成实际的包路径。
 *
 * @author redcoder54
 * @since 2021-08-13
 */
class TemplateSourceCodeProjectContributor implements ProjectContributor {

    private static final String rootResource = "classpath:template/sourcecode";
    private static final String sourceCodeDirPrefix = "src" + File.separatorChar + "main" + File.separatorChar + "java";
    private static final String packagePlaceholder = "${package}";

    private final ProjectDescription description;
    private final PathMatchingResourcePatternResolver resolver;

    public TemplateSourceCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.resolver = new PathMatchingResourcePatternResolver();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        String basePackagePath = description.getPackageName().replace('.', File.separatorChar);
        Resource root = this.resolver.getResource(rootResource);
        Resource[] resources = this.resolver.getResources(rootResource + "/**");
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                String filename = extractFileName(root.getURI(), resource.getURI());
                Path output = projectRoot.resolve(addParentPath(basePackagePath, filename));
                Files.createDirectories(output.getParent());
                Files.createFile(output);
                writeJavaSourceCodeFile(resource.getInputStream(), Files.newOutputStream(output));
            }
        }
    }

    /**
     * 获取resource表示的文件相对路径
     */
    private String extractFileName(URI root, URI resource) {
        String candidate = resource.toString().substring(root.toString().length());
        return StringUtils.trimLeadingCharacter(candidate, '/');
    }

    /**
     * 添加父路径：src/main/java/{basePackagePath}
     */
    private String addParentPath(String basePackagePath, String fileName) {
        return sourceCodeDirPrefix + File.separatorChar + basePackagePath + File.separatorChar + fileName;
    }

    public void writeJavaSourceCodeFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.contains(packagePlaceholder)) {
                    line = line.replace(packagePlaceholder, description.getPackageName());
                }
                writer.write(line);
                writer.write("\n");
            }
        }
    }

}
