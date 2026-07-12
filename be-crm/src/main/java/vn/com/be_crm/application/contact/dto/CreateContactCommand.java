package vn.com.be_crm.application.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.ContactGender;

import java.time.LocalDate;
import java.util.List;

/** Input DTO khi tạo mới liên hệ. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateContactCommand {
    private Long customerId;
    private Long assignedUserId;
    @Size(max = 10) private String salutation;
    @NotBlank(message = "Họ tên không được để trống") @Size(max = 100) private String fullName;
    @Size(max = 100) private String title;
    @Size(max = 100) private String department;
    @Size(max = 100) private String position;
    @Size(max = 50) @Email(message = "Email không hợp lệ") private String email;
    @Size(max = 100) @Email(message = "Email không hợp lệ") private String workEmail;
    @Size(max = 100) @Email(message = "Email không hợp lệ") private String personalEmail;
    @Size(max = 20) private String zalo;
    @Size(max = 30) private String source;
    private ContactGender gender;
    private LocalDate dateOfBirth;
    @Size(max = 255) private String address;
    private Boolean doNotCall;
    private Boolean doNotEmail;
    private Boolean isPrimary;
    /** Danh sách số điện thoại tạo kèm liên hệ (contactId bỏ trống). */
    @Valid private List<CreateContactPhoneCommand> phones;
}
