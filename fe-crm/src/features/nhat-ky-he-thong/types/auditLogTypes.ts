/** Một dòng nhật ký sự kiện thao tác người dùng (GET /api/audit-log). */
export interface AuditLogEntry {
    source: string;
    actorName: string | null;
    action: string;
    targetLabel: string | null;
    note: string | null;
    occurredAt: string;
}

/** Tab lọc theo nguồn dữ liệu — mỗi nguồn ứng với một (hoặc nhóm) bảng đã có trong DB. */
export const AUDIT_SOURCE_TABS: { id: string | null; label: string }[] = [
    { id: null, label: 'Tất cả' },
    { id: 'quotation_approval', label: 'Duyệt báo giá' },
    { id: 'lead_transfer', label: 'Bàn giao tiềm năng' },
    { id: 'ticket_comment', label: 'Phiếu chăm sóc' },
    { id: 'notification', label: 'Thông báo hệ thống' },
    { id: 'record_change', label: 'Tạo/sửa/xóa bản ghi' },
];
