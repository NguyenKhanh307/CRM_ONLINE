package vn.com.be_crm.domain.opportunity.repository;

import vn.com.be_crm.application.opportunity.dto.BoardColumnResult;

import java.util.List;

/**
 * Port đọc dữ liệu bảng Kanban cơ hội (chỉ đọc, native SQL — giống IDashboardRepository).
 */
public interface IOpportunityBoardRepository {

    /**
     * Lấy toàn bộ cột (giai đoạn) kèm thẻ cơ hội, đếm và tổng tiền mỗi cột.
     *
     * @param ownerId           null = xem tất cả (admin/manager); ngược lại chỉ cơ hội của nhân viên này
     * @param dataAccessFromYear năm sớm nhất được xem (null = không giới hạn)
     * @param q                 từ khóa tìm kiếm theo mã/tên cơ hội (null/rỗng = không lọc)
     * @return danh sách cột theo sort_order
     */
    List<BoardColumnResult> getBoard(Long ownerId, Integer dataAccessFromYear, String q);
}
