package ${package}.constant;

/**
 * api请求错误码定义
 */
public enum ApiStatus {

    OK(0, "ok"),

    // 业务端错误
    PARAM_ERROR(1001, "参数错误"),
    BAD_REQUEST(1002, "无法受理的请求"),

    // 系统内部错误
    SERVER_ERROR(5000, "服务繁忙"),

    ;

    private final int status;
    private final String message;

    ApiStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
