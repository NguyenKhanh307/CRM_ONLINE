package vn.com.be_crm.application.customer.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.customer.enums.CustomerStatus;
import vn.com.be_crm.domain.customer.enums.CustomerType;

import java.math.BigDecimal;

/** Input DTO khi tạo mới khách hàng. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerCommand {
    @NotBlank(message = "Mã khách hàng không được để trống")
    @Size(max = 20)
    private String code;
    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 100)
    private String name;
    @Size(max = 50)
    private String shortName;
    private CustomerType type;
    @Size(max = 15)
    @Pattern(regexp = "^$|^[0-9-]{10,14}$", message = "Mã số thuế không hợp lệ (10-14 chữ số)")
    private String taxCode;
    @Size(max = 11)
    @Pattern(regexp = "^$|^[0-9+.() -]{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    @Size(max = 50)
    @Email(message = "Email không hợp lệ")
    private String email;
    @Size(max = 100)
    private String website;
    @Size(max = 255)
    private String address;
    @Size(max = 50)
    private String industry;
    @Size(max = 20)
    private String source;
    private CustomerStatus status;
    @PositiveOrZero(message = "Số ngày nợ không được âm")
    private Integer creditDays;
    @PositiveOrZero(message = "Hạn mức tín dụng không được âm")
    private BigDecimal creditLimit;
    @Size(max = 30)
    private String bankAccount;
    @Size(max = 100)
    private String bankName;
    @Size(max = 10)
    private String rating;
    @PositiveOrZero(message = "Doanh thu năm không được âm")
    private BigDecimal annualRevenue;
    @Size(max = 30)
    private String employeeSize;
    private Boolean isDistributor;
    private Long ownerId;
}
