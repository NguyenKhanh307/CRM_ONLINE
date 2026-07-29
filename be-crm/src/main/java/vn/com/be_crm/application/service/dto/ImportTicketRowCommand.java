package vn.com.be_crm.application.service.dto;

/** Một dòng dữ liệu Ticket từ file import — chỉ gồm field header (không đụng status/SLA/CSAT, đổi qua hành động). */
public record ImportTicketRowCommand(
        String code,
        String type,
        String subject,
        String description,
        Long customerId,
        Long contactId,
        Long invoiceId,
        Long productId,
        String channel,
        String priority,
        String reason,
        Long assignedUserId,
        Long ownerId,
        String ownerEmail
) {}
