package vn.com.be_crm.application.customer.mapper;

import vn.com.be_crm.application.customer.dto.*;
import vn.com.be_crm.domain.customer.entity.InventoryCheck;
import vn.com.be_crm.domain.customer.enums.InventoryCheckStatus;

/** Chuyển đổi Command ↔ InventoryCheck ↔ InventoryCheckResult. */
public class InventoryCheckCommandMapper {

    /**
     * Tạo InventoryCheck từ CreateInventoryCheckCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static InventoryCheck toEntity(CreateInventoryCheckCommand cmd) {
        return InventoryCheck.builder()
                .code(cmd.getCode()).customerId(cmd.getCustomerId())
                .checkedBy(cmd.getCheckedBy()).checkDate(cmd.getCheckDate())
                .status(cmd.getStatus() != null ? cmd.getStatus() : InventoryCheckStatus.draft)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật InventoryCheck từ UpdateInventoryCheckCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static InventoryCheck toEntity(UpdateInventoryCheckCommand cmd, InventoryCheck e) {
        return InventoryCheck.builder()
                .id(e.getId()).code(e.getCode()).customerId(e.getCustomerId())
                .checkedBy(cmd.getCheckedBy() != null ? cmd.getCheckedBy() : e.getCheckedBy())
                .checkDate(cmd.getCheckDate() != null ? cmd.getCheckDate() : e.getCheckDate())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển InventoryCheck sang InventoryCheckResult.
     * @param e domain entity @return result DTO
     */
    public static InventoryCheckResult toResult(InventoryCheck e) {
        return InventoryCheckResult.builder()
                .id(e.getId()).code(e.getCode()).customerId(e.getCustomerId())
                .checkedBy(e.getCheckedBy()).checkDate(e.getCheckDate()).status(e.getStatus())
                .note(e.getNote()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private InventoryCheckCommandMapper() {}
}
