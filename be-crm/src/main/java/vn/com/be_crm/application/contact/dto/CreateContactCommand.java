package vn.com.be_crm.application.contact.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.ContactGender;

import java.time.LocalDate;

/** Input DTO khi tạo mới liên hệ. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateContactCommand {
    private Long customerId;
    private Long assignedUserId;
    @NotBlank(message = "Họ tên không được để trống") @Size(max = 100) private String fullName;
    @Size(max = 100) private String position;
    @Size(max = 50) private String email;
    private ContactGender gender;
    private LocalDate dateOfBirth;
    @Size(max = 255) private String address;
    private Boolean isPrimary;
}
