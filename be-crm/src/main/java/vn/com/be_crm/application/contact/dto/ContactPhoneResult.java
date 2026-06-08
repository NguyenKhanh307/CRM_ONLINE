package vn.com.be_crm.application.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.PhoneType;

import java.time.LocalDateTime;

/** Output DTO cho ContactPhone. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class ContactPhoneResult {
    private Long id;
    private Long contactId;
    private String phone;
    private PhoneType phoneType;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
