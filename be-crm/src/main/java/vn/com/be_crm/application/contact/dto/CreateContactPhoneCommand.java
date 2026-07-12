package vn.com.be_crm.application.contact.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.PhoneType;

/** Input DTO khi tạo mới số điện thoại liên hệ. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateContactPhoneCommand {
    /** ID liên hệ — controller set từ path; bỏ trống khi tạo nested kèm liên hệ. */
    private Long contactId;
    @NotBlank(message = "Số điện thoại không được để trống") @Size(max = 11) @Pattern(regexp = "^$|^[0-9+.() -]{8,15}$", message = "Số điện thoại không hợp lệ") private String phone;
    private PhoneType phoneType;
    private Boolean isPrimary;
}
