package vn.com.be_crm.application.customer.mapper;

import vn.com.be_crm.application.customer.dto.*;
import vn.com.be_crm.domain.customer.entity.Customer;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;

/** Chuyển đổi Command ↔ Customer ↔ CustomerResult. */
public class CustomerCommandMapper {

    /**
     * Tạo Customer từ CreateCustomerCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Customer toEntity(CreateCustomerCommand cmd) {
        return Customer.builder()
                .code(cmd.getCode()).name(cmd.getName())
                .type(cmd.getType() != null ? cmd.getType() : CustomerType.company)
                .taxCode(cmd.getTaxCode()).phone(cmd.getPhone()).email(cmd.getEmail())
                .address(cmd.getAddress()).source(cmd.getSource())
                .status(cmd.getStatus() != null ? cmd.getStatus() : CustomerStatus.active)
                .ownerId(cmd.getOwnerId()).unitId(cmd.getUnitId()).build();
    }

    /**
     * Cập nhật Customer từ UpdateCustomerCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
    public static Customer toEntity(UpdateCustomerCommand cmd, Customer e) {
        return Customer.builder()
                .id(e.getId()).code(e.getCode())
                .name(cmd.getName() != null ? cmd.getName() : e.getName())
                .type(cmd.getType() != null ? cmd.getType() : e.getType())
                .taxCode(cmd.getTaxCode() != null ? cmd.getTaxCode() : e.getTaxCode())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : e.getPhone())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .address(cmd.getAddress() != null ? cmd.getAddress() : e.getAddress())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                .status(cmd.getStatus() != null ? cmd.getStatus() : e.getStatus())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .unitId(cmd.getUnitId() != null ? cmd.getUnitId() : e.getUnitId())
                .createdAt(e.getCreatedAt()).build();
    }

    /**
     * Chuyển Customer sang CustomerResult.
     * @param e domain entity @return result DTO
     */
    public static CustomerResult toResult(Customer e) {
        return CustomerResult.builder()
                .id(e.getId()).code(e.getCode()).name(e.getName()).type(e.getType())
                .taxCode(e.getTaxCode()).phone(e.getPhone()).email(e.getEmail())
                .address(e.getAddress()).source(e.getSource()).status(e.getStatus())
                .ownerId(e.getOwnerId()).unitId(e.getUnitId())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private CustomerCommandMapper() {}
}
