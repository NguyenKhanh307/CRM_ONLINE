package vn.com.be_crm.application.dashboard.dto;

/**
 * Một mục việc gấp cần chú ý (phiếu quá hạn SLA, báo giá sắp hết hạn, hóa đơn quá hạn).
 *
 * @param type    loại: "ticket" | "quotation" | "invoice"
 * @param refId   ID bản ghi
 * @param code    mã bản ghi
 * @param label   mô tả ngắn
 * @param dueInfo thông tin hạn (vd "Quá hạn 2 ngày")
 */
public record UrgentItem(String type, Long refId, String code, String label, String dueInfo) {
}
