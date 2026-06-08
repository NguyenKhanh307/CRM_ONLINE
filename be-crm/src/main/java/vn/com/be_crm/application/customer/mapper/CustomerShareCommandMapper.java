package vn.com.be_crm.application.customer.mapper;

import vn.com.be_crm.application.customer.dto.AssignCustomerShareCommand;
import vn.com.be_crm.application.customer.dto.CustomerShareResult;
import vn.com.be_crm.domain.customer.entity.CustomerShare;
import vn.com.be_crm.domain.customer.enums.CustomerSharePermission;

/** Chuyển đổi Command ↔ CustomerShare ↔ CustomerShareResult. */
public class CustomerShareCommandMapper {

    /**
     * Tạo CustomerShare từ AssignCustomerShareCommand.
     * @param cmd command gán chia sẻ @return domain entity
     */
    public static CustomerShare toEntity(AssignCustomerShareCommand cmd) {
        return CustomerShare.builder()
                .customerId(cmd.getCustomerId()).userId(cmd.getUserId())
                .permission(cmd.getPermission() != null ? cmd.getPermission() : CustomerSharePermission.view)
                .sharedBy(cmd.getSharedBy()).build();
    }

    /**
     * Chuyển CustomerShare sang CustomerShareResult.
     * @param e domain entity @return result DTO
     */
    public static CustomerShareResult toResult(CustomerShare e) {
        return CustomerShareResult.builder()
                .id(e.getId()).customerId(e.getCustomerId()).userId(e.getUserId())
                .permission(e.getPermission()).sharedBy(e.getSharedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    private CustomerShareCommandMapper() {}
}
