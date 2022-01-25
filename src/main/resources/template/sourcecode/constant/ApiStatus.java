package ${package}.constant;

/**
 * api请求错误码定义
 */
public enum ApiStatus {

    OK(0, "ok"),

    // 客户端的问题导致的错误
    PARAM_ERROR(101, "参数错误"),
    BAD_REQUEST(102, "无法受理的请求"),
    UNKNOWN_USER(103, "用户身份校验失败"),

    // 系统内部错误
    SERVER_ERROR(500, "服务繁忙"),

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
