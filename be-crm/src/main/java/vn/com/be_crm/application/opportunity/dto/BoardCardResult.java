package vn.com.be_crm.application.opportunity.dto;

import java.math.BigDecimal;

// một thẻ cơ hội trên bảng Kanban — id/code/name/customerName/ownerName/amount/stageId (=cột chứa thẻ)
public record BoardCardResult(
        Long id,
        String code,
        String name,
        String customerName,
        String ownerName,
        BigDecimal amount,
        Long stageId) {
}
