package vn.com.be_crm.domain.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.ReturnReason;

import java.math.BigDecimal;

/**
 * Domain entity dòng hàng trả/đổi (ticket_return_items).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketReturnItem {
    /** ID dòng. */
    private Long id;
    /** ID phiếu. */
    private Long ticketId;
    /** ID dòng hóa đơn gốc. */
    private Long invoiceItemId;
    /** ID sản phẩm. */
    private Long productId;
    /** Số lượng trả/đổi. */
    private BigDecimal quantity;
    /** Đơn giá. */
    private BigDecimal unitPrice;
    /** Giá trị trả/hoàn. */
    private BigDecimal amount;
    /** Lý do trả/đổi. */
    private ReturnReason reason;
    /** Tình trạng hàng khi kiểm (inspected). */
    private String conditionNote;
}
