// loại yêu cầu sau bán
export type TicketType = 'support' | 'return' | 'exchange' | 'complaint';
// kênh tiếp nhận
export type TicketChannel = 'phone' | 'email' | 'web' | 'zalo' | 'other';
// độ ưu tiên
export type TicketPriority = 'low' | 'medium' | 'high' | 'urgent';
// trạng thái xử lý
export type TicketStatus =
    | 'new' | 'assigned' | 'in_progress' | 'approved' | 'rejected'
    | 'received' | 'inspected' | 'resolved' | 'closed' | 'reopened';
// lý do trả/đổi/khiếu nại
export type ReturnReason =
    | 'defective' | 'wrong_item' | 'not_as_described' | 'changed_mind' | 'late_delivery' | 'other';
// hình thức giải quyết
export type ResolutionType =
    | 'refund' | 'replacement' | 'repair' | 'store_credit' | 'answered' | 'rejected';

// phiếu hỗ trợ trả về từ GET /api/tickets/{id}. Không còn customerId/contactId/invoiceId/productId
// trực tiếp — chỉ còn orderId, mọi liên kết khác tra qua chuỗi Ticket -> Order -> Quotation
export interface TicketResult {
    id: number;
    code: string;
    type: TicketType;
    subject: string;
    description: string | null;
    orderId: number | null;
    channel: TicketChannel;
    priority: TicketPriority;
    status: TicketStatus;
    reason: ReturnReason | null;
    resolutionType: ResolutionType | null;
    resolutionNote: string | null;
    assignedUserId: number | null;
    slaPolicyId: number | null;
    firstResponseAt: string | null;
    slaDueAt: string | null;
    resolvedAt: string | null;
    closedAt: string | null;
    satisfactionScore: number | null;
    satisfactionComment: string | null;
    // suy ra từ BE: quá hạn SLA
    isOverdue: boolean;
    createdAt: string;
    updatedAt: string;
    // Tên khóa ngoại do BE resolve sẵn (INameResolver).
    orderCode: string | null;
    assignedUserName: string | null;
    // Audit: BE tự đóng dấu (AuditInterceptor).
    createdBy: number | null;
    updatedBy: number | null;
    createdByName: string | null;
    updatedByName: string | null;
}

// một dòng hàng trả/đổi gửi kèm khi tạo phiếu — chỉ còn invoiceItemId/quantity/reason/conditionNote
export interface TicketReturnItemPayload {
    invoiceItemId: number | null;
    quantity: number;
    reason: ReturnReason | null;
    conditionNote: string | null;
}

// dòng hàng trả/đổi trả về từ GET /api/tickets/{id}/return-items
export interface TicketReturnItemResult {
    id: number;
    ticketId: number;
    invoiceItemId: number | null;
    quantity: number;
    reason: ReturnReason | null;
    conditionNote: string | null;
}

// payload tạo mới phiếu — POST /api/tickets (kèm returnItems[] nếu trả/đổi)
export interface CreateTicketPayload {
    code: string;
    type: TicketType;
    subject: string;
    description: string | null;
    orderId: number | null;
    channel: TicketChannel;
    priority: TicketPriority;
    reason: ReturnReason | null;
    assignedUserId: number | null;
    returnItems: TicketReturnItemPayload[];
}

// payload cập nhật phiếu — KHÔNG gửi status (đổi qua hành động)
export interface UpdateTicketPayload {
    type: TicketType;
    subject: string;
    description: string | null;
    orderId: number | null;
    channel: TicketChannel;
    priority: TicketPriority;
    reason: ReturnReason | null;
    assignedUserId: number | null;
}
