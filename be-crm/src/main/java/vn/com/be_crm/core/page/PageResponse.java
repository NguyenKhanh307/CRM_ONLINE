package vn.com.be_crm.core.page;

import vn.com.be_crm.core.page.PageResult;

import java.util.List;

/**
 * Wrapper JSON cho kết quả phân trang, bao gồm metadata trang.
 *
 * @param <T>        kiểu item
 * @param items      danh sách item trang hiện tại
 * @param total      tổng số bản ghi
 * @param page       số trang hiện tại (0-based)
 * @param size       số bản ghi mỗi trang
 * @param totalPages tổng số trang
 */
public record PageResponse<T>(
        List<T> items,
        long total,
        int page,
        int size,
        int totalPages
) {

    /**
     * Chuyển đổi từ PageResult domain sang PageResponse JSON.
     *
     * @param result PageResult từ UseCase
     * @param <T>    kiểu item
     * @return PageResponse sẵn sàng trả về HTTP
     */
    public static <T> PageResponse<T> from(PageResult<T> result) {
        return new PageResponse<>(
                result.getItems(),
                result.getTotal(),
                result.getPage(),
                result.getSize(),
                result.getTotalPages()
        );
    }
}
