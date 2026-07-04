package vn.com.be_crm.application.order.dto;

import java.math.BigDecimal;

/** Một dòng dữ liệu Order từ file import. */
public record ImportOrderRowCommand(
        String code,
        String orderDate,
        String deliveryDate,
        String status,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total,
        String note,
        Long ownerId,
        String ownerEmail
) {}
