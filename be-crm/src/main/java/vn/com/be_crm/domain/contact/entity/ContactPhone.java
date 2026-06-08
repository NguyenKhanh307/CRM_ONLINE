package vn.com.be_crm.domain.contact.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.PhoneType;

import java.time.LocalDateTime;

/**
 * Domain entity đại diện cho số điện thoại liên hệ.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactPhone {
    private Long id;
    private Long contactId;
    private String phone;
    private PhoneType phoneType;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
