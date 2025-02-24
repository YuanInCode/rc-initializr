package redcoder.rcinitializr.generator.project.contributor.support;

import io.spring.initializr.generator.buildsystem.*;
import io.spring.initializr.generator.buildsystem.maven.*;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class PrettyMavenBuildWriter {

    /**
     * Write a {@linkplain MavenBuild pom.xml} using the specified
     * {@linkplain IndentingWriter writer}.
     *
     * @param writer the writer to use
     * @param build  the maven build to write
     */
    public void writeTo(IndentingWriter writer, MavenBuild build) {
        MavenBuildSettings settings = build.getSettings();
        writeProject(writer, () -> {
            writeParent(writer, build);
            writer.println();

            writeProjectCoordinates(writer, settings);
            writePackaging(writer, settings);
            writeProjectName(writer, settings);
            writer.println();

            writeCollectionElement(writer, "licenses", settings.getLicenses(), this::writeLicense);
            writer.println();

            writeCollectionElement(writer, "developers", settings.getDevelopers(), this::writeDeveloper);
            writer.println();

            writeScm(writer, settings.getScm());
            writer.println();

            writeProperties(writer, build.properties());
            writer.println();

            writeDependencies(writer, build.dependencies());
            writer.println();

            writeDependencyManagement(writer, build.boms());
            writer.println();

            writeBuild(writer, build);
            writer.println();

            writeRepositories(writer, build.repositories(), build.pluginRepositories());
            writer.println();

            writeDistributionManagement(writer, build.getDistributionManagement());
            writer.println();

            writeProfiles(writer, build);
            writer.println();
        });
    }

    private void writeProject(IndentingWriter writer, Runnable whenWritten) {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println(
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        writer.indented(() -> {
            writer.println(
                    "xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">");
            writeSingleElement(writer, "modelVersion", "4.0.0");
            whenWritten.run();
        });
        writer.println();
        writer.println("</project>");
    }

    private void writeParent(IndentingWriter writer, MavenBuild build) {
        MavenParent parent = build.getSettings().getParent();
        if (parent == null) {
            return;
        }
        writer.println("<parent>");
        writer.indented(() -> {
            writeSingleElement(writer, "groupId", parent.getGroupId());
            writeSingleElement(writer, "artifactId", parent.getArtifactId());
            writeSingleElement(writer, "version", parent.getVersion());
            writer.println("<relativePath/> <!-- lookup parent from repository -->");
        });
        writer.println("</parent>");
    }

    private void writeProjectCoordinates(IndentingWriter writer, MavenBuildSettings settings) {
        writeSingleElement(writer, "groupId", settings.getGroup());
        writeSingleElement(writer, "artifactId", settings.getArtifact());
        writeSingleElement(writer, "version", settings.getVersion());
    }

    private void writePackaging(IndentingWriter writer, MavenBuildSettings settings) {
        String packaging = settings.getPackaging();
        if (!"jar".equals(packaging)) {
            writeSingleElement(writer, "packaging", packaging);
        }
    }

    private void writeProjectName(IndentingWriter writer, MavenBuildSettings settings) {
        writeSingleElement(writer, "name", settings.getName());
        writeSingleElement(writer, "description", settings.getDescription());
    }

    private void writeProperties(IndentingWriter writer, PropertyContainer properties) {
        if (properties.isEmpty()) {
            return;
        }
        writeElement(writer, "properties", () -> {
            properties.values().forEach((entry) -> writeSingleElement(writer, entry.getKey(), entry.getValue()));
            properties.versions((VersionProperty::toStandardFormat))
                    .forEach((entry) -> writeSingleElement(writer, entry.getKey(), entry.getValue()));
        });
    }

    private void writeLicense(IndentingWriter writer, MavenLicense license) {
        writeElement(writer, "license", () -> {
            writeSingleElement(writer, "name", license.getName());
            writeSingleElement(writer, "url", license.getUrl());
            if (license.getDistribution() != null) {
                writeSingleElement(writer, "distribution",
                        license.getDistribution().name().toLowerCase(Locale.ENGLISH));
            }
            writeSingleElement(writer, "comments", license.getComments());
        });
    }

    private void writeDeveloper(IndentingWriter writer, MavenDeveloper developer) {
        writeElement(writer, "developer", () -> {
            writeSingleElement(writer, "id", developer.getId());
            writeSingleElement(writer, "name", developer.getName());
            writeSingleElement(writer, "email", developer.getEmail());
            writeSingleElement(writer, "url", developer.getUrl());
            writeSingleElement(writer, "organization", developer.getOrganization());
            writeSingleElement(writer, "organizationUrl", developer.getOrganizationUrl());
            List<String> roles = developer.getRoles();
            if (!roles.isEmpty()) {
                writeElement(writer, "roles", () -> roles.forEach((role) -> writeSingleElement(writer, "role", role)));
            }
            writeSingleElement(writer, "timezone", developer.getTimezone());
            Map<String, String> properties = developer.getProperties();
            if (!properties.isEmpty()) {
                writeElement(writer, "properties",
                        () -> properties.forEach((key, value) -> writeSingleElement(writer, key, value)));
            }
        });
    }

    private void writeScm(IndentingWriter writer, MavenScm mavenScm) {
        if (!mavenScm.isEmpty()) {
            writeElement(writer, "scm", () -> {
                writeSingleElement(writer, "connection", mavenScm.getConnection());
                writeSingleElement(writer, "developerConnection", mavenScm.getDeveloperConnection());
                writeSingleElement(writer, "tag", mavenScm.getTag());
                writeSingleElement(writer, "url", mavenScm.getUrl());
            });
        }
    }

    private void writeDependencies(IndentingWriter writer, DependencyContainer dependencies) {
        if (dependencies.isEmpty()) {
            return;
        }
        writeElement(writer, "dependencies", () -> {
            // group by groupId
            TreeMap<String, List<Dependency>> map = dependencies.items().collect(groupingBy(Dependency::getGroupId, TreeMap::new, toList()));
            for (Map.Entry<String, List<Dependency>> entry : map.entrySet()) {
                List<Dependency> candidates = entry.getValue();
                writeCollection(writer, candidates, this::writeDependency);
                writer.println();
            }
        });
    }

    private void writeDependency(IndentingWriter writer, Dependency dependency) {
        writeElement(writer, "dependency", () -> {
            writeSingleElement(writer, "groupId", dependency.getGroupId());
            writeSingleElement(writer, "artifactId", dependency.getArtifactId());
            writeSingleElement(writer, "version", determineVersion(dependency.getVersion()));
            writeSingleElement(writer, "scope", scopeForType(dependency.getScope()));
            writeSingleElement(writer, "classifier", dependency.getClassifier());
            if (isOptional(dependency)) {
                writeSingleElement(writer, "optional", Boolean.toString(true));
            }
            writeSingleElement(writer, "type", dependency.getType());
            writeCollectionElement(writer, "exclusions", dependency.getExclusions(), this::writeDependencyExclusion);
        });
    }

    private void writeDependencyExclusion(IndentingWriter writer, Dependency.Exclusion exclusion) {
        writeElement(writer, "exclusion", () -> {
            writeSingleElement(writer, "groupId", exclusion.getGroupId());
            writeSingleElement(writer, "artifactId", exclusion.getArtifactId());
        });
    }

    private String scopeForType(DependencyScope type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case ANNOTATION_PROCESSOR:
                return null;
            case COMPILE:
                return null;
            case COMPILE_ONLY:
                return null;
            case PROVIDED_RUNTIME:
                return "provided";
            case RUNTIME:
                return "runtime";
            case TEST_COMPILE:
                return "test";
            case TEST_RUNTIME:
                return "test";
            default:
                throw new IllegalStateException("Unrecognized dependency type '" + type + "'");
        }
    }

    private boolean isOptional(Dependency dependency) {
        if (dependency instanceof MavenDependency && ((MavenDependency) dependency).isOptional()) {
            return true;
        }
        return (dependency.getScope() == DependencyScope.ANNOTATION_PROCESSOR
                || dependency.getScope() == DependencyScope.COMPILE_ONLY);
    }

    private void writeDependencyManagement(IndentingWriter writer, BomContainer boms) {
        if (boms.isEmpty()) {
            return;
        }
        writeElement(writer, "dependencyManagement",
                () -> writeCollectionElement(writer, "dependencies", boms.items()
                                .sorted(Comparator.comparing(BillOfMaterials::getOrder)).collect(toList()),
                        this::writeBom));
    }

    private void writeBom(IndentingWriter writer, BillOfMaterials bom) {
        writeElement(writer, "dependency", () -> {
            writeSingleElement(writer, "groupId", bom.getGroupId());
            writeSingleElement(writer, "artifactId", bom.getArtifactId());
            writeSingleElement(writer, "version", determineVersion(bom.getVersion()));
            writeSingleElement(writer, "type", "pom");
            writeSingleElement(writer, "scope", "import");
        });
    }

    private String determineVersion(VersionReference versionReference) {
        if (versionReference == null) {
            return null;
        }
        return (versionReference.isProperty()) ? "${" + versionReference.getProperty().toStandardFormat() + "}"
                : versionReference.getValue();
    }

    private void writeBuild(IndentingWriter writer, MavenBuild build) {
        MavenBuildSettings settings = build.getSettings();
        if (settings.getDefaultGoal() == null && settings.getFinalName() == null
                && settings.getSourceDirectory() == null && settings.getTestSourceDirectory() == null
                && build.resources().isEmpty() && build.testResources().isEmpty() && build.plugins().isEmpty()) {
            return;
        }
        writer.println();
        writeElement(writer, "build", () -> {
            writeSingleElement(writer, "defaultGoal", settings.getDefaultGoal());
            writeSingleElement(writer, "finalName", settings.getFinalName());
            writeSingleElement(writer, "sourceDirectory", settings.getSourceDirectory());
            writeSingleElement(writer, "testSourceDirectory", settings.getTestSourceDirectory());
            writeResources(writer, build.resources(), build.testResources());
            writeCollectionElement(writer, "plugins", build.plugins().values(), this::writePlugin);
        });
    }

    private void writeResources(IndentingWriter writer, MavenResourceContainer resources,
                                MavenResourceContainer testResources) {
        writeCollectionElement(writer, "resources", resources.values(), this::writeResource);
        writeCollectionElement(writer, "testResources", testResources.values(), this::writeTestResource);
    }

    private void writeResource(IndentingWriter writer, MavenResource resource) {
        writeResource(writer, resource, "resource");
    }

    private void writeTestResource(IndentingWriter writer, MavenResource resource) {
        writeResource(writer, resource, "testResource");
    }

    private void writeResource(IndentingWriter writer, MavenResource resource, String resourceName) {
        writeElement(writer, resourceName, () -> {
            writeSingleElement(writer, "directory", resource.getDirectory());
            writeSingleElement(writer, "targetPath", resource.getTargetPath());
            if (resource.isFiltering()) {
                writeSingleElement(writer, "filtering", "true");
            }
            writeCollectionElement(writer, "includes", resource.getIncludes(), this::writeResourceInclude);
            writeCollectionElement(writer, "excludes", resource.getExcludes(), this::writeResourceExclude);
        });
    }

    private void writeResourceInclude(IndentingWriter writer, String include) {
        writeSingleElement(writer, "include", include);
    }

    private void writeResourceExclude(IndentingWriter writer, String exclude) {
        writeSingleElement(writer, "exclude", exclude);
    }

    private void writePlugin(IndentingWriter writer, MavenPlugin plugin) {
        writeElement(writer, "plugin", () -> {
            writeSingleElement(writer, "groupId", plugin.getGroupId());
            writeSingleElement(writer, "artifactId", plugin.getArtifactId());
            writeSingleElement(writer, "version", plugin.getVersion());
            if (plugin.isExtensions()) {
                writeSingleElement(writer, "extensions", "true");
            }
            writePluginConfiguration(writer, plugin.getConfiguration());
            writeCollectionElement(writer, "executions", plugin.getExecutions(), this::writePluginExecution);
            writeCollectionElement(writer, "dependencies", plugin.getDependencies(), this::writePluginDependency);
        });
    }

    private void writePluginConfiguration(IndentingWriter writer, MavenPlugin.Configuration configuration) {
        if (configuration == null || configuration.getSettings().isEmpty()) {
            return;
        }
        writeCollectionElement(writer, "configuration", configuration.getSettings(), this::writeSetting);
    }

    @SuppressWarnings("unchecked")
    private void writeSetting(IndentingWriter writer, MavenPlugin.Setting setting) {
        if (setting.getValue() instanceof String) {
            writeSingleElement(writer, setting.getName(), setting.getValue());
        } else if (setting.getValue() instanceof List) {
            writeCollectionElement(writer, setting.getName(), (List<MavenPlugin.Setting>) setting.getValue(), this::writeSetting);
        }
    }

    private void writePluginExecution(IndentingWriter writer, MavenPlugin.Execution execution) {
        writeElement(writer, "execution", () -> {
            writeSingleElement(writer, "id", execution.getId());
            writeSingleElement(writer, "phase", execution.getPhase());
            List<String> goals = execution.getGoals();
            if (!goals.isEmpty()) {
                writeElement(writer, "goals", () -> goals.forEach((goal) -> writeSingleElement(writer, "goal", goal)));
            }
            writePluginConfiguration(writer, execution.getConfiguration());
        });
    }

    private void writePluginDependency(IndentingWriter writer, MavenPlugin.Dependency dependency) {
        writeElement(writer, "dependency", () -> {
            writeSingleElement(writer, "groupId", dependency.getGroupId());
            writeSingleElement(writer, "artifactId", dependency.getArtifactId());
            writeSingleElement(writer, "version", dependency.getVersion());
        });
    }

    private void writeRepositories(IndentingWriter writer, MavenRepositoryContainer buildRepositories,
                                   MavenRepositoryContainer buildPluginRepositories) {
        List<MavenRepository> repositories = filterRepositories(buildRepositories.items());
        List<MavenRepository> pluginRepositories = filterRepositories(buildPluginRepositories.items());
        if (repositories.isEmpty() && pluginRepositories.isEmpty()) {
            return;
        }
        writeCollectionElement(writer, "repositories", repositories, this::writeRepository);
        writeCollectionElement(writer, "pluginRepositories", pluginRepositories, this::writePluginRepository);
    }

    private List<MavenRepository> filterRepositories(Stream<MavenRepository> repositories) {
        return repositories.filter((repository) -> !MavenRepository.MAVEN_CENTRAL.equals(repository))
                .collect(toList());
    }

    private void writeRepository(IndentingWriter writer, MavenRepository repository) {
        writeRepository(writer, repository, "repository");
    }

    private void writePluginRepository(IndentingWriter writer, MavenRepository repository) {
        writeRepository(writer, repository, "pluginRepository");
    }

    private void writeRepository(IndentingWriter writer, MavenRepository repository, String childName) {
        writeElement(writer, childName, () -> {
            writeSingleElement(writer, "id", repository.getId());
            writeSingleElement(writer, "name", repository.getName());
            writeSingleElement(writer, "url", repository.getUrl());
            if (repository.isSnapshotsEnabled()) {
                writeElement(writer, "snapshots", () -> writeSingleElement(writer, "enabled", Boolean.toString(true)));
            }
        });
    }

    private void writeDistributionManagement(IndentingWriter writer,
                                             MavenDistributionManagement distributionManagement) {
        if (distributionManagement.isEmpty()) {
            return;
        }
        writeElement(writer, "distributionManagement", () -> {
            writeSingleElement(writer, "downloadUrl", distributionManagement.getDownloadUrl());
            writeDeploymentRepository(writer, "repository", distributionManagement.getRepository());
            writeDeploymentRepository(writer, "snapshotRepository", distributionManagement.getSnapshotRepository());
            MavenDistributionManagement.Site site = distributionManagement.getSite();
            if (!site.isEmpty()) {
                writeElement(writer, "site", () -> {
                    writeSingleElement(writer, "id", site.getId());
                    writeSingleElement(writer, "name", site.getName());
                    writeSingleElement(writer, "url", site.getUrl());
                });
            }
            MavenDistributionManagement.Relocation relocation = distributionManagement.getRelocation();
            if (!relocation.isEmpty()) {
                writeElement(writer, "relocation", () -> {
                    writeSingleElement(writer, "groupId", relocation.getGroupId());
                    writeSingleElement(writer, "artifactId", relocation.getArtifactId());
                    writeSingleElement(writer, "version", relocation.getVersion());
                    writeSingleElement(writer, "message", relocation.getMessage());
                });
            }
        });
    }

    private void writeDeploymentRepository(IndentingWriter writer, String name, MavenDistributionManagement.DeploymentRepository repository) {
        if (!repository.isEmpty()) {
            writeElement(writer, name, () -> {
                writeSingleElement(writer, "id", repository.getId());
                writeSingleElement(writer, "name", repository.getName());
                writeSingleElement(writer, "url", repository.getUrl());
                writeSingleElement(writer, "layout", repository.getLayout());
                if (repository.getUniqueVersion() != null) {
                    writeSingleElement(writer, "uniqueVersion", Boolean.toString(repository.getUniqueVersion()));
                }
            });
        }
    }

    private void writeProfiles(IndentingWriter writer, MavenBuild build) {
        MavenProfileContainer profiles = build.profiles();
        if (profiles.isEmpty()) {
            return;
        }
        writer.println();
        writeElement(writer, "profiles", () -> profiles.values().forEach((profile) -> writeProfile(writer, profile)));
    }

    private void writeProfile(IndentingWriter writer, MavenProfile profile) {
        writeElement(writer, "profile", () -> {
            writeSingleElement(writer, "id", profile.getId());
            writeProfileActivation(writer, profile.getActivation());
            writeProperties(writer, profile.properties());
            writeDependencies(writer, profile.dependencies());
            writeDependencyManagement(writer, profile.boms());
            writeProfileBuild(writer, profile);
            writeRepositories(writer, profile.repositories(), profile.pluginRepositories());
            writeDistributionManagement(writer, profile.getDistributionManagement());
        });
    }

    private void writeProfileActivation(IndentingWriter writer, MavenProfileActivation activation) {
        if (activation.isEmpty()) {
            return;
        }
        writeElement(writer, "activation", () -> {
            writeSingleElement(writer, "activeByDefault", activation.getActiveByDefault());
            writeSingleElement(writer, "jdk", activation.getJdk());
            ifNotNull(activation.getOs(), (os) -> writeElement(writer, "os", () -> {
                writeSingleElement(writer, "name", os.getName());
                writeSingleElement(writer, "arch", os.getArch());
                writeSingleElement(writer, "family", os.getFamily());
                writeSingleElement(writer, "version", os.getVersion());
            }));
            ifNotNull(activation.getProperty(), (property) -> writeElement(writer, "property", () -> {
                writeSingleElement(writer, "name", property.getName());
                writeSingleElement(writer, "value", property.getValue());
            }));
            ifNotNull(activation.getFile(), (file) -> writeElement(writer, "file", () -> {
                writeSingleElement(writer, "exists", file.getExists());
                writeSingleElement(writer, "missing", file.getMissing());
            }));
        });
    }

    private void writeProfileBuild(IndentingWriter writer, MavenProfile profile) {
        MavenProfile.Settings settings = profile.getSettings();
        if (settings.getDefaultGoal() == null && settings.getFinalName() == null && profile.resources().isEmpty()
                && profile.testResources().isEmpty() && profile.plugins().isEmpty()) {
            return;
        }
        writeElement(writer, "build", () -> {
            writeSingleElement(writer, "defaultGoal", settings.getDefaultGoal());
            writeSingleElement(writer, "finalName", settings.getFinalName());
            writeResources(writer, profile.resources(), profile.testResources());
            writeCollectionElement(writer, "plugins", profile.plugins().values(), this::writePlugin);
        });
    }

    private void writeSingleElement(IndentingWriter writer, String name, Object value) {
        if (value != null) {
            CharSequence text = (value instanceof CharSequence) ? (CharSequence) value : value.toString();
            writer.print(String.format("<%s>", name));
            writer.print(encodeText(text));
            writer.println(String.format("</%s>", name));
        }
    }

    private void writeElement(IndentingWriter writer, String name, Runnable withContent) {
        writer.println(String.format("<%s>", name));
        writer.indented(withContent);
        writer.println(String.format("</%s>", name));
    }

    private <T> void writeCollectionElement(IndentingWriter writer, String name, Stream<T> items,
                                            BiConsumer<IndentingWriter, T> itemWriter) {
        writeCollectionElement(writer, name, items.collect(toList()), itemWriter);
    }

    private <T> void writeCollectionElement(IndentingWriter writer, String name, Collection<T> items,
                                            BiConsumer<IndentingWriter, T> itemWriter) {
        if (!ObjectUtils.isEmpty(items)) {
            writeElement(writer, name, () -> writeCollection(writer, items, itemWriter));
        }
    }

    private <T> void writeCollection(IndentingWriter writer, Collection<T> collection,
                                     BiConsumer<IndentingWriter, T> itemWriter) {
        if (!collection.isEmpty()) {
            collection.forEach((item) -> itemWriter.accept(writer, item));
        }
    }

    private <T> void ifNotNull(T value, Consumer<T> elementWriter) {
        if (value != null) {
            elementWriter.accept(value);
        }
    }

    private String encodeText(CharSequence text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            switch (character) {
                case '\'':
                    sb.append("&apos;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(character);
            }
        }
        return sb.toString();
    }

}
