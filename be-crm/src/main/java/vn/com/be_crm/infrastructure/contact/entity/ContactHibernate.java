package vn.com.be_crm.infrastructure.contact.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.com.be_crm.domain.contact.enums.ContactGender;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hibernate entity ánh xạ bảng contacts.
 */
@Entity
@Table(name = "contacts")
@Getter @Setter @NoArgsConstructor
public class ContactHibernate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "assigned_user_id")
    private Long assignedUserId;
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    @Column(name = "position", length = 100)
    private String position;
    @Column(name = "email", length = 50)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private ContactGender gender;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "address", length = 255)
    private String address;
    @Column(name = "is_primary")
    private Boolean isPrimary;
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
