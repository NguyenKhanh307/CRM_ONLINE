package vn.com.be_crm.presentation.lead.request;

import lombok.Getter;
import lombok.Setter;

// body tùy chọn của hành động convert tiềm năng — khi FE phát hiện khách hàng/liên hệ trùng và
// người dùng chọn "dùng bản ghi hiện có", FE gửi ID tương ứng để convert không tạo bản ghi mới
@Getter
@Setter
public class ConvertLeadRequest {
    // null = tạo mới
    private Long customerId;
    private Long contactId;
}
