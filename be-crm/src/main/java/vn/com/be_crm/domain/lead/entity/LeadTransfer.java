package vn.com.be_crm.domain.lead.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// lịch sử chuyển giao (bàn giao) một tiềm năng
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadTransfer {
    private Long id;
    private Long leadId;
    private Long fromUserId;
    private Long toUserId;
    private String reason;
    private LocalDateTime transferredAt;
}
