package vn.com.be_crm.application.opportunity.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Một thẻ cơ hội trên bảng Kanban.
 *
 * @param id                ID cơ hội
 * @param code              mã cơ hội
 * @param name              tên cơ hội
 * @param customerName      tên khách hàng (resolve sẵn ở SQL)
 * @param ownerName         tên người phụ trách (resolve sẵn ở SQL)
 * @param amount            giá trị cơ hội
 * @param expectedCloseDate ngày dự kiến chốt
 * @param probability       xác suất thắng (%)
 * @param stageId           giai đoạn hiện tại (= cột chứa thẻ)
 */
public record BoardCardResult(
        Long id,
        String code,
        String name,
        String customerName,
        String ownerName,
        BigDecimal amount,
        LocalDate expectedCloseDate,
        BigDecimal probability,
        Long stageId) {
}
