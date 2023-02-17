package ${package}.dto;

import ${package}.constant.ApiStatus;
import static ${package}.constant.ApiStatus.OK;

public class ApiResult<T> {

    /**
     * 请求结果码，0：请求成功，其它：失败
     */
    private Integer status;

    /**
     * 请求结果描述
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public ApiResult() {
    }

    public ApiResult(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ApiResult<T> success() {
        ApiResult<T> result = new ApiResult<>();
        result.setStatus(OK.getStatus());
        result.setMessage(OK.getMessage());
        return result;
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setStatus(OK.getStatus());
        result.setMessage(OK.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> ApiResult<T> failure(int code, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setStatus(code);
        result.setMessage(message);
        return result;
    }

    public static <T> ApiResult<T> failure(ApiStatus apiStatus) {
        return new ApiResult<>(apiStatus.getStatus(), apiStatus.getMessage());
    }
}
