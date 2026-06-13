package vn.com.be_crm.application.lead.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

import java.math.BigDecimal;

/** Input DTO khi tạo mới tiềm năng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateLeadCommand {
    @NotBlank(message = "Mã tiềm năng không được để trống") @Size(max = 20) private String code;
    @NotBlank(message = "Tên tiềm năng không được để trống") @Size(max = 100) private String name;
    @Size(max = 100) private String companyName;
    @Size(max = 30) private String leadType;
    private Long ownerId;
    private Long customerId;
    private Long contactId;
    @Size(max = 100) private String title;
    @Size(max = 100) private String department;
    @Size(max = 15) private String taxCode;
    @Size(max = 100) private String website;
    @Size(max = 50) private String industry;
    @Size(max = 20) private String source;
    private LeadStatus status;
    private BigDecimal estimatedValue;
    @Size(max = 11) private String phone;
    @Size(max = 50) private String email;
    private Boolean doNotCall;
    private Boolean doNotEmail;
    @Size(max = 255) private String note;
}
