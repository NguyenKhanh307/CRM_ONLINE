package vn.com.be_crm.application.contact.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.PhoneType;

/** Input DTO khi tạo mới số điện thoại liên hệ. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateContactPhoneCommand {
    @NotNull(message = "Liên hệ không được để trống") private Long contactId;
    @NotBlank(message = "Số điện thoại không được để trống") @Size(max = 11) private String phone;
    private PhoneType phoneType;
    private Boolean isPrimary;
}
