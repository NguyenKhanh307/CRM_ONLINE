package vn.com.be_crm.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Wrapper chuẩn cho tất cả HTTP response của API.
 *
 * @param <T>     kiểu dữ liệu trả về
 * @param data    dữ liệu chính
 * @param message thông báo mô tả
 * @param status  HTTP status code
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(T data, String message, int status) {

    /**
     * Tạo response thành công với dữ liệu.
     *
     * @param data dữ liệu trả về
     * @param <T>  kiểu dữ liệu
     * @return ApiResponse 200 OK
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data, "Success", 200);
    }

    /**
     * Tạo response thành công khi tạo mới.
     *
     * @param data dữ liệu vừa tạo
     * @param <T>  kiểu dữ liệu
     * @return ApiResponse 201 Created
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(data, "Created", 201);
    }

    /**
     * Tạo response lỗi.
     *
     * @param message mô tả lỗi
     * @param status  HTTP status code lỗi
     * @param <T>     kiểu dữ liệu (thường là Void)
     * @return ApiResponse lỗi
     */
    public static <T> ApiResponse<T> error(String message, int status) {
        return new ApiResponse<>(null, message, status);
    }
}
