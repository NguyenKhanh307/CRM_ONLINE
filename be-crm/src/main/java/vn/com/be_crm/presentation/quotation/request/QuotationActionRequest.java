package vn.com.be_crm.presentation.quotation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body cho các hành động duyệt báo giá (approve/reject) — chứa ý kiến/lý do tùy chọn.
 */
@Getter
@Setter
@NoArgsConstructor
public class QuotationActionRequest {
    /** Ý kiến khi duyệt hoặc lý do khi từ chối. */
    private String comment;
}
