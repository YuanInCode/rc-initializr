package redcoder.rcinitializr.generator.project.contributor;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import redcoder.rcinitializr.generator.project.contributor.support.PrettyMavenBuildWriter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class PrettyMavenBuildProjectContributor implements BuildWriter, ProjectContributor {

    private final MavenBuild build;

    private final IndentingWriterFactory indentingWriterFactory;

    private final PrettyMavenBuildWriter buildWriter;

    public PrettyMavenBuildProjectContributor(MavenBuild build, IndentingWriterFactory indentingWriterFactory) {
        this.build = build;
        this.indentingWriterFactory = indentingWriterFactory;
        this.buildWriter = new PrettyMavenBuildWriter();
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path pomFile = Files.createFile(projectRoot.resolve("pom.xml"));
        writeBuild(Files.newBufferedWriter(pomFile));
    }

    @Override
    public void writeBuild(Writer out) throws IOException {
        try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("maven", out)) {
            this.buildWriter.writeTo(writer, this.build);
        }
    }

}
