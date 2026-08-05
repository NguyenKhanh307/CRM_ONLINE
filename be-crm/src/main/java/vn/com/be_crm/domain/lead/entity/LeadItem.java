package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadItemInterestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// sản phẩm mà tiềm năng quan tâm/đã yêu cầu báo giá — landing page ghi lại để khi tạo Cơ hội/
// Báo giá riêng cho lead này, người dùng có sẵn danh sách sản phẩm để chọn thay vì gõ lại từ đầu
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadItem {
    private Long id;
    private Long leadId;
    private Long productId;
    private BigDecimal quantity;
    private LeadItemInterestType interestType;
    private LocalDateTime createdAt;
}
