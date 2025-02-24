package redcoder.rcinitializr.generator.project.contributor;

import io.spring.initializr.generator.project.contributor.MultipleResourcesProjectContributor;

import java.util.function.Predicate;

/**
 * 添加CI脚本到项目根目录下
 */
public class BuildScriptProjectContributor extends MultipleResourcesProjectContributor {

    public BuildScriptProjectContributor() {
        super("classpath:template/buildscript", Predicate.<String>isEqual("rcbuilder.bat")
                .or(Predicate.isEqual("rcbuidler.sh")));
    }
}
