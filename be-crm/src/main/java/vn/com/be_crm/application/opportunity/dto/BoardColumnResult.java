package vn.com.be_crm.application.opportunity.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Một cột (= giai đoạn pipeline) trên bảng Kanban.
 * Tên trường cờ đặt là {@code won}/{@code lost} (không phải {@code isWon}) để JSON không mập mờ.
 *
 * @param stageId   ID giai đoạn
 * @param stageName tên giai đoạn
 * @param sortOrder thứ tự cột
 * @param won       true nếu là giai đoạn thắng
 * @param lost      true nếu là giai đoạn thua
 * @param total     tổng số cơ hội thật ở giai đoạn này (có thể lớn hơn cards.size())
 * @param sumAmount tổng giá trị cơ hội ở giai đoạn này
 * @param cards     tối đa 50 thẻ mới cập nhật nhất
 */
public record BoardColumnResult(
        Long stageId,
        String stageName,
        Integer sortOrder,
        boolean won,
        boolean lost,
        long total,
        BigDecimal sumAmount,
        List<BoardCardResult> cards) {
}
