package redcoder.rcinitializr.controller;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.WebProjectRequest;

import java.util.Map;

/**
 * @author redcoder54
 * @since 2021-08-13
 */
// @RestController
public class RcMarketProjectGenerationController extends ProjectGenerationController<ProjectRequest> {

    public RcMarketProjectGenerationController(InitializrMetadataProvider metadataProvider,
                                               ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);
    }

    @Override
    public ProjectRequest projectRequest(Map<String, String> headers) {
        WebProjectRequest request = new WebProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());
        return request;
    }
}
