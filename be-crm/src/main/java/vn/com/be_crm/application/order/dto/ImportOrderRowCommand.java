package vn.com.be_crm.application.order.dto;

// một dòng dữ liệu Order từ file import
public record ImportOrderRowCommand(
        String code,
        Long quotationId,
        String orderDate,
        String deliveryDate,
        String status,
        String note,
        Long ownerId,
        String ownerEmail
) {}
