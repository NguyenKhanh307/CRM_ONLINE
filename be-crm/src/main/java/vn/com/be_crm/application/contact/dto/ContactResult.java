package vn.com.be_crm.application.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.contact.enums.ContactGender;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Output DTO cho Contact. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class ContactResult {
    private Long id;
    private Long customerId;
    private Long assignedUserId;
    private String fullName;
    private String position;
    private String email;
    private ContactGender gender;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
