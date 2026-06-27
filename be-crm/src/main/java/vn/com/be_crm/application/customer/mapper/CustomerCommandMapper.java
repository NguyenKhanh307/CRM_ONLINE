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
                .code(cmd.getCode()).name(cmd.getName()).shortName(cmd.getShortName())
                .type(cmd.getType() != null ? cmd.getType() : CustomerType.company)
                .taxCode(cmd.getTaxCode()).phone(cmd.getPhone()).email(cmd.getEmail())
                .website(cmd.getWebsite()).address(cmd.getAddress())
                .industry(cmd.getIndustry()).source(cmd.getSource())
                .status(cmd.getStatus() != null ? cmd.getStatus() : CustomerStatus.active)
                .creditDays(cmd.getCreditDays()).creditLimit(cmd.getCreditLimit())
                .bankAccount(cmd.getBankAccount()).bankName(cmd.getBankName())
                .rating(cmd.getRating()).annualRevenue(cmd.getAnnualRevenue())
                .employeeSize(cmd.getEmployeeSize())
                .isDistributor(cmd.getIsDistributor() != null && cmd.getIsDistributor())
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
                .shortName(cmd.getShortName() != null ? cmd.getShortName() : e.getShortName())
                .type(cmd.getType() != null ? cmd.getType() : e.getType())
                .taxCode(cmd.getTaxCode() != null ? cmd.getTaxCode() : e.getTaxCode())
                .phone(cmd.getPhone() != null ? cmd.getPhone() : e.getPhone())
                .email(cmd.getEmail() != null ? cmd.getEmail() : e.getEmail())
                .website(cmd.getWebsite() != null ? cmd.getWebsite() : e.getWebsite())
                .address(cmd.getAddress() != null ? cmd.getAddress() : e.getAddress())
                .industry(cmd.getIndustry() != null ? cmd.getIndustry() : e.getIndustry())
                .source(cmd.getSource() != null ? cmd.getSource() : e.getSource())
                // status: đổi qua hành động (activate/deactivate) — không nhận từ command.
                .status(e.getStatus())
                .creditDays(cmd.getCreditDays() != null ? cmd.getCreditDays() : e.getCreditDays())
                .creditLimit(cmd.getCreditLimit() != null ? cmd.getCreditLimit() : e.getCreditLimit())
                .bankAccount(cmd.getBankAccount() != null ? cmd.getBankAccount() : e.getBankAccount())
                .bankName(cmd.getBankName() != null ? cmd.getBankName() : e.getBankName())
                .rating(cmd.getRating() != null ? cmd.getRating() : e.getRating())
                .annualRevenue(cmd.getAnnualRevenue() != null ? cmd.getAnnualRevenue() : e.getAnnualRevenue())
                .employeeSize(cmd.getEmployeeSize() != null ? cmd.getEmployeeSize() : e.getEmployeeSize())
                .isDistributor(cmd.getIsDistributor() != null ? cmd.getIsDistributor() : e.isDistributor())
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
                .id(e.getId()).code(e.getCode()).name(e.getName()).shortName(e.getShortName())
                .type(e.getType())
                .taxCode(e.getTaxCode()).phone(e.getPhone()).email(e.getEmail())
                .website(e.getWebsite()).address(e.getAddress())
                .industry(e.getIndustry()).source(e.getSource()).status(e.getStatus())
                .creditDays(e.getCreditDays()).creditLimit(e.getCreditLimit())
                .bankAccount(e.getBankAccount()).bankName(e.getBankName())
                .rating(e.getRating()).annualRevenue(e.getAnnualRevenue())
                .employeeSize(e.getEmployeeSize()).isDistributor(e.isDistributor())
                .ownerId(e.getOwnerId()).unitId(e.getUnitId())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    private CustomerCommandMapper() {}
}
