package vn.com.be_crm.domain.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.service.enums.ReturnReason;

import java.math.BigDecimal;

// dòng hàng trả/đổi (ticket_return_items). Không còn productId/unitPrice/amount — giá trị hàng
// trả suy từ invoice_item_id liên kết.
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketReturnItem {
    private Long id;
    private Long ticketId;
    // ID dòng hóa đơn gốc
    private Long invoiceItemId;
    // số lượng trả/đổi
    private BigDecimal quantity;
    private ReturnReason reason;
    // tình trạng hàng khi kiểm (inspected)
    private String conditionNote;
}
