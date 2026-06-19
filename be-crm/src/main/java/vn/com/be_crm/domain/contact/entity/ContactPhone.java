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
    /** ID số điện thoại. */
    private Long id;
    /** ID liên hệ. */
    private Long contactId;
    /** Số điện thoại. */
    private String phone;
    /** Loại số (di động/cố định/fax...). */
    private PhoneType phoneType;
    /** Là số điện thoại chính. */
    private Boolean isPrimary;
    /** Thời điểm tạo. */
    private LocalDateTime createdAt;
}
