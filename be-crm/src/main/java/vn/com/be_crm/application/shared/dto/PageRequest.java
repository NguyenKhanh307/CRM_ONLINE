package vn.com.be_crm.application.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Tham số phân trang và sắp xếp dùng cho tất cả List UseCase.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    /** Số trang bắt đầu từ 0. */
    @Builder.Default
    private int page = 0;

    /** Số bản ghi mỗi trang. */
    @Builder.Default
    private int size = 20;

    /** Tên cột sắp xếp (tên field Java, không phải tên cột DB). */
    @Builder.Default
    private String sortBy = "id";

    /** Chiều sắp xếp: "asc" hoặc "desc". */
    @Builder.Default
    private String sortDir = "asc";

    /** @return offset = page * size */
    public int getOffset() {
        return page * size;
    }
}
