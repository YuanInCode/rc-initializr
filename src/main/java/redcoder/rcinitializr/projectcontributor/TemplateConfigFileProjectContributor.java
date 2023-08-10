package redcoder.rcinitializr.projectcontributor;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.StringJoiner;

import static redcoder.rcinitializr.projectcontributor.TemplateConfigFileProjectContributor.SpringConfigFilePlaceHolder.*;

/**
 * 1. 添加以下配置文件至项目的resources目录下：
 * <ul>
 *     <li>application.yaml (copy from application.yam)</li>
 *     <li>application-DEV.yaml (copy from application-DEV.yam)</li>
 *     <li>application-ZPTEST.yaml (copy from application-ZPTEST.yam)</li>
 *     <li>application-PROD.yaml (copy from application-PROD.yam)</li>
 *     <li>logback-spring.xml</li>
 * </ul>
 * <p>
 *     2.替换配置文件中的占位符：${controllerPackage}, ${artifactId}, ${version}, ${quartzSchedulerName}
 * </p>
 *
 * <p>
 *     3.删除已有的application.properties文件；
 * </p>
 *
 * @author redcoder54
 * @since 2021-08-13
 */
class TemplateConfigFileProjectContributor implements ProjectContributor {

    private static final Logger log = LoggerFactory.getLogger(TemplateConfigFileProjectContributor.class);

    // 配置文件所在目录
    private static final String rootResource = "classpath:template/configfile";
    // maven项目资源文件目录，src/main/resources
    private static final String resourcesDirPrefix = "src" + File.separatorChar + "main" + File.separatorChar + "resources";
    // spring initializr生成的配置文件名称
    private static final String defaultConfigFile = "application.properties";

    private final ProjectDescription description;
    private final PathMatchingResourcePatternResolver resolver;

    public TemplateConfigFileProjectContributor(ProjectDescription description) {
        this.description = description;
        this.resolver = new PathMatchingResourcePatternResolver();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Resource root = this.resolver.getResource(rootResource);
        Resource[] resources = this.resolver.getResources(rootResource + "/**");
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                String filename = extractFileName(root.getURI(), resource.getURI());
                Path output = projectRoot.resolve(addParentPath(filename));
                Files.createDirectories(output.getParent());
                Files.createFile(output);
                InputStream inputStream = processingConfigFile(resource.getInputStream(), filename);
                FileCopyUtils.copy(inputStream, Files.newOutputStream(output));
            }
        }

        // 删除application.properties文件
        File f = getDefaultConfigFile(projectRoot.toFile());
        if (f != null && f.delete()) {
            log.debug("delete file '{}' successfully", defaultConfigFile);
        }
    }

    private String extractFileName(URI root, URI resource) {
        String candidate = resource.toString().substring(root.toString().length());
        String filename = StringUtils.trimLeadingCharacter(candidate, '/');
        if (filename.endsWith("tpl")) {
            filename = filename.substring(0, filename.indexOf(".")) + ".yml";
        }
        return filename;
    }

    /**
     * 替换配置文件中的占位符
     */
    private InputStream processingConfigFile(InputStream inputStream, String filename) throws IOException {
        if (filename.endsWith("yml")) {
            return processingSpringConfigFile(inputStream);
        } else {
            return inputStream;
        }
    }

    /**
     * 替换spring配置文件中的占位符
     */
    private InputStream processingSpringConfigFile(InputStream inputStream) throws IOException {
        StringJoiner fileContent = new StringJoiner("\n");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (line.contains(appName)) {
                line = line.replace(appName, description.getApplicationName());
            } else if (line.contains(controllerPackage)) {
                line = line.replace(controllerPackage, description.getPackageName() + ".controller");
            } else if (line.contains(version)) {
                line = line.replace(version, description.getVersion());
            } else if (line.contains(artifactId)) {
                line = line.replace(artifactId, description.getArtifactId());
            } else if (line.contains(quartzSchedulerName)) {
                line = line.replace(quartzSchedulerName, toCamelCase(description.getArtifactId()) + "Scheduler");
            }
            fileContent.add(line);
        }
        return new ByteArrayInputStream(fileContent.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String toCamelCase(String artifactId) {
        if (!artifactId.contains("-")) {
            return artifactId;
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = artifactId.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c != '-') {
                sb.append(c);
            } else {
                c = chars[++i];
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * 添加父路径：src/main/resources
     */
    private String addParentPath(String fileName) {
        return resourcesDirPrefix + File.separatorChar + fileName;
    }

    public File getDefaultConfigFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    File target = getDefaultConfigFile(f);
                    if (target != null) {
                        return target;
                    }
                }
            }
        } else if (file.isFile() && Objects.equals(defaultConfigFile, file.getName())) {
            return file;
        }
        return null;
    }

    /**
     * mybatis配置文件占位符定义
     */
    static final class MybatisConfigFilePlaceHolder {

        /**
         * maven project root package path
         */
        static final String packageName = "${package}";
    }

    /**
     * spring配置文件中的占位符定义
     */
    static final class SpringConfigFilePlaceHolder {

        /**
         * 应用名称
         */
        static final String appName = "${appName}";
        /**
         * controller包类路径
         */
        static final String controllerPackage = "${controllerPackage}";
        /**
         * maven project artifact
         */
        static final String artifactId = "${artifactId}";
        /**
         * maven project version
         */
        static final String version = "${version}";
        /**
         * quartz实例名称
         */
        static final String quartzSchedulerName = "${quartzSchedulerName}";
    }
}
