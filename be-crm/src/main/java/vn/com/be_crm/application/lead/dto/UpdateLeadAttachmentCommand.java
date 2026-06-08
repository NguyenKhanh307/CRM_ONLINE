package vn.com.be_crm.application.lead.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Input DTO khi cập nhật tệp đính kèm tiềm năng. */
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateLeadAttachmentCommand {
    private Long id;
    @Size(max = 30) private String fileName;
    @Size(max = 255) private String fileUrl;
    private Integer fileSize;
    @Size(max = 100) private String mimeType;
}
