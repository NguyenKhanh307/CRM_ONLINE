package vn.com.be_crm.application.lead.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.com.be_crm.domain.lead.enums.LeadStatus;

// input khi cập nhật tiềm năng
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateLeadCommand {
    // id lấy từ path (@PathVariable), KHÔNG @NotNull — nếu không mọi request update sẽ 400
    private Long id;
    @Size(max = 100) private String name;
    @Size(max = 100) private String companyName;
    @Size(max = 30) private String leadType;
    private Long ownerId;
    private Long contactId;
    // gán tay bản ghi cơ hội đã tạo riêng cho tiềm năng này (không còn tự sinh qua convert)
    private Long convertedOpportunityId;
    @Size(max = 15) @Pattern(regexp = "^$|^[0-9-]{10,14}$", message = "Mã số thuế không hợp lệ (10-14 chữ số)") private String taxCode;
    @Size(max = 100) private String website;
    @Size(max = 50) private String industry;
    @Size(max = 20) private String source;
    private Long campaignId;
    // trạng thái "converted" cũng tự đổi tay tại đây — không còn action /convert riêng
    private LeadStatus status;
    @Size(max = 11) @Pattern(regexp = "^$|^[0-9+.() -]{8,15}$", message = "Số điện thoại không hợp lệ") private String phone;
    @Size(max = 50) @Email(message = "Email không hợp lệ") private String email;
    @Size(max = 255) private String note;
}
