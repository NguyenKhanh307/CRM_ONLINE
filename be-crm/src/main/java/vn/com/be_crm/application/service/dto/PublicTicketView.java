package vn.com.be_crm.application.service.dto;

import java.time.LocalDateTime;

/**
 * Dữ liệu phiếu chăm sóc hiển thị trên trang public để khách tự xem trạng thái + đánh giá
 * (không cần đăng nhập). Chỉ chứa thông tin công khai — không có tên khách hàng/liên hệ/người
 * xử lý hay ghi chú nội bộ.
 *
 * @param code                mã phiếu
 * @param type                loại phiếu (support/return/exchange/complaint)
 * @param status               trạng thái xử lý
 * @param priority             độ ưu tiên
 * @param subject              tiêu đề
 * @param description          mô tả
 * @param createdAt            ngày tạo
 * @param slaDueAt             hạn giải quyết theo SLA
 * @param resolvedAt           thời điểm giải quyết xong
 * @param closedAt             thời điểm đóng phiếu
 * @param satisfactionScore    điểm hài lòng đã chấm (null nếu chưa)
 * @param satisfactionComment  nhận xét đã gửi (null nếu chưa)
 */
public record PublicTicketView(
        String code,
        String type,
        String status,
        String priority,
        String subject,
        String description,
        LocalDateTime createdAt,
        LocalDateTime slaDueAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt,
        Integer satisfactionScore,
        String satisfactionComment
) {}
