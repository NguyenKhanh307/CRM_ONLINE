package vn.com.be_crm.infrastructure.contact.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import vn.com.be_crm.domain.contact.enums.PhoneType;

import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng contact_phones.
 */
@Entity
@Table(name = "contact_phones")
@Getter @Setter @NoArgsConstructor
public class ContactPhoneHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "contact_id", nullable = false)
    private Long contactId;
    @Column(name = "phone", nullable = false, length = 11)
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(name = "phone_type", length = 10)
    private PhoneType phoneType;
    @Column(name = "is_primary")
    private Boolean isPrimary;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
