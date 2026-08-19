package vn.com.be_crm.infrastructure.contact.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.contact.enums.ContactGender;

import vn.com.be_crm.core.audit.IAuditable;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng contacts.
 */
@Entity
@Table(name = "contacts")
@Getter @Setter @NoArgsConstructor
public class ContactHibernate implements IAuditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "assigned_user_id")
    private Long assignedUserId;
    @Column(name = "salutation", length = 10)
    private String salutation;
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    @Column(name = "title", length = 100)
    private String title;
    @Column(name = "department", length = 100)
    private String department;
    @Column(name = "email", length = 50)
    private String email;
    @Column(name = "zalo", length = 20)
    private String zalo;
    @Column(name = "phone", length = 11)
    private String phone;
    @Column(name = "source", length = 30)
    private String source;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private ContactGender gender;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "is_primary")
    private Boolean isPrimary;
    // updatable = false: created_by không bao giờ vào câu UPDATE → merge() không thể NULL đè
    @Column(name = "created_by", updatable = false) private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "deleted_by") private Long deletedBy;
    @Column(name = "is_purged") private boolean isPurged;
}
