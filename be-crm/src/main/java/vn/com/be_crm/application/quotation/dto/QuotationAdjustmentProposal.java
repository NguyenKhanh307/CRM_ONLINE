package vn.com.be_crm.application.quotation.dto;

import java.math.BigDecimal;
import java.util.List;

// Cấu trúc JSON của đề xuất chỉnh sửa dòng hàng do khách gửi từ trang công khai — lưu thẳng vào cột
// có sẵn quotations.customer_response_note (KHÔNG thêm cột DB mới). ProposeQuotationAdjustmentUseCase
// ghi giá trị này; CreateQuotationFromAdjustmentUseCase đọc lại khi nhân viên bấm "Tạo báo giá mới
// theo yêu cầu khách". items = danh sách dòng hàng khách muốn GIỮ LẠI kèm số lượng mới; dòng khách
// xóa thì không có mặt.
public record QuotationAdjustmentProposal(String note, List<Item> items) {
    public record Item(Long id, BigDecimal quantity) {}
}
