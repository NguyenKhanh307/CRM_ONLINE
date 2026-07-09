package vn.com.be_crm.application.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.be_crm.domain.contact.enums.ContactGender;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho Contact. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ContactResult {
    private Long id;
    private Long customerId;
    private Long assignedUserId;
    private String salutation;
    private String fullName;
    private String title;
    private String department;
    private String position;
    private String email;
    private String workEmail;
    private String personalEmail;
    private String zalo;
    private String source;
    private ContactGender gender;
    private LocalDate dateOfBirth;
    private String address;
    private boolean doNotCall;
    private boolean doNotEmail;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Tên khóa ngoại — do BE resolve (INameResolver).
    private String customerName;
    private String assignedUserName;
}
