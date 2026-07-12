package vn.com.be_crm.application.contact.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.PhoneType;

/** Input DTO khi cập nhật số điện thoại liên hệ. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateContactPhoneCommand {
    private Long id;
    @Size(max = 11) @Pattern(regexp = "^$|^[0-9+.() -]{8,15}$", message = "Số điện thoại không hợp lệ") private String phone;
    private PhoneType phoneType;
    private Boolean isPrimary;
}
