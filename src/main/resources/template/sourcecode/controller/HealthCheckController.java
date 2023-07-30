package ${package}.controller;

import ${package}.dto.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ApiResult<String> healthCheck() {
        return ApiResult.success("ok");
    }
}
