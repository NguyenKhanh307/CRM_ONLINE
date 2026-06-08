package vn.com.be_crm.application.lead.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi tạo mới tệp đính kèm tiềm năng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateLeadAttachmentCommand {
    @NotNull private Long leadId;
    @NotBlank @Size(max = 30) private String fileName;
    @NotBlank @Size(max = 255) private String fileUrl;
    private Integer fileSize;
    @Size(max = 100) private String mimeType;
    private Long uploadedBy;
}
