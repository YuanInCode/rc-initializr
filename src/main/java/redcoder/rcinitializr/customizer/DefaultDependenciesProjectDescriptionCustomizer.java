package redcoder.rcinitializr.customizer;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescriptionCustomizer;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 添加一些默认的依赖
 *
 * @author redcoder54
 * @since 2021-08-14
 */
public class DefaultDependenciesProjectDescriptionCustomizer implements ProjectDescriptionCustomizer {
    private final Set<String> requiredDependenciesId;
    private final InitializrMetadataProvider metadataProvider;

    public DefaultDependenciesProjectDescriptionCustomizer(InitializrMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
        // 默认添加的依赖
        requiredDependenciesId = new HashSet<>();
        requiredDependenciesId.add("web");
        requiredDependenciesId.add("data-redis");
        requiredDependenciesId.add("jedis");
        requiredDependenciesId.add("quartz");
        requiredDependenciesId.add("validation");
        requiredDependenciesId.add("mysql");
        requiredDependenciesId.add("mybatis");
        requiredDependenciesId.add("mybatis-generator-core");
        requiredDependenciesId.add("tk-mybatis-mapper");
        requiredDependenciesId.add("apache-commons-pool");
        requiredDependenciesId.add("apache-commons-lang3");
        requiredDependenciesId.add("commons-collections");
        requiredDependenciesId.add("lombok");
        requiredDependenciesId.add("kafka");
        requiredDependenciesId.add("guava");
        requiredDependenciesId.add("h2database");
    }

    @Override
    public void customize(MutableProjectDescription description) {
        Map<String, Dependency> dependencies = description.getRequestedDependencies();
        Set<String> ids = dependencies.keySet();
        Set<String> diff = diff(requiredDependenciesId, ids);
        // 添加依赖
        InitializrMetadata metadata = metadataProvider.get();
        diff.forEach(id -> {
            io.spring.initializr.metadata.Dependency dependency = metadata.getDependencies().get(id);
            description.addDependency(id, MetadataBuildItemMapper.toDependency(dependency));
        });
    }

    /**
     * 计算sources和targets的差集：存在于sources中，但不在targets中。
     */
    private Set<String> diff(Set<String> sources, Set<String> targets) {
        Set<String> diff = new HashSet<>();
        for (String source : sources) {
            if (!targets.contains(source)) {
                diff.add(source);
            }
        }
        return diff;
    }
}
