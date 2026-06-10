package vn.com.be_crm.application.shared.dto;

/** Thông tin lỗi của một dòng trong quá trình nhập hàng loạt. */
public record ImportRowError(int row, String message) {}
