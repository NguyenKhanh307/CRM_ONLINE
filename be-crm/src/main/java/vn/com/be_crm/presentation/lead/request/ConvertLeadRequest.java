package vn.com.be_crm.presentation.lead.request;

import lombok.Getter;
import lombok.Setter;

/**
 * HTTP body (tùy chọn) của hành động convert tiềm năng.
 * Khi FE phát hiện khách hàng/liên hệ trùng và người dùng chọn "dùng bản ghi hiện có",
 * FE gửi ID tương ứng để convert không tạo bản ghi mới.
 */
@Getter
@Setter
public class ConvertLeadRequest {

    /** Khách hàng đã có để gắn cơ hội vào (null = tạo khách hàng mới). */
    private Long customerId;

    /** Liên hệ đã có để gắn cơ hội vào (null = tạo liên hệ mới). */
    private Long contactId;
}
