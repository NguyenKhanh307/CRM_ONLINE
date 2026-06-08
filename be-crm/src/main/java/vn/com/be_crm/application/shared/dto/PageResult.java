package vn.com.be_crm.application.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Kết quả phân trang trả về từ List UseCase.
 *
 * @param <T> kiểu item trong danh sách
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** Danh sách item của trang hiện tại. */
    private List<T> items;

    /** Tổng số bản ghi (không phân trang). */
    private long total;

    /** Số trang hiện tại (bắt đầu từ 0). */
    private int page;

    /** Số bản ghi mỗi trang. */
    private int size;

    /** @return tổng số trang */
    public int getTotalPages() {
        return size == 0 ? 0 : (int) Math.ceil((double) total / size);
    }
}
