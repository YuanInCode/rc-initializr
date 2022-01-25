package ${package}.controller;

import ${package}.model.dto.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/info/desc")
    @SuppressWarnings("rawtypes")
    public ApiResult healthCheck() {
        return ApiResult.success();
    }
}
