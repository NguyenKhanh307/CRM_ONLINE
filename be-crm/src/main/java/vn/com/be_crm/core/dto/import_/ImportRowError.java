package vn.com.be_crm.core.dto.import_;

/** Thông tin lỗi của một dòng trong quá trình nhập hàng loạt. */
public record ImportRowError(int row, String message) {}
