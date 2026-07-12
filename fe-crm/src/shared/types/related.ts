/** Phân hệ của một bản ghi liên quan — dùng để deep-link sang trang tương ứng. */
export type RelatedModule =
    | 'contact'
    | 'opportunity'
    | 'quotation'
    | 'order'
    | 'invoice'
    | 'ticket'
    | 'activity';

/**
 * Một dòng bản ghi liên quan trên trang chi tiết 360°.
 * Shape chung cho mọi phân hệ — mỗi tab tự chọn hiển thị cột nào (xem `RelatedTable`).
 */
export interface RelatedRecord {
    module: RelatedModule;
    id: number;
    /** Mã bản ghi, hoặc giá trị phụ: email (liên hệ), loại hoạt động (call/meeting…). */
    code: string | null;
    name: string | null;
    /** Trạng thái, hoặc nhãn phụ: chức danh (liên hệ). */
    status: string | null;
    /** ISO datetime từ BE, vd "2026-07-12T10:00:00". */
    date: string | null;
    amount: number | null;
    ownerName: string | null;
}

/** Nhóm bản ghi liên quan: danh sách rút gọn (tối đa 50 dòng) + tổng số thật trong DB. */
export interface RelatedGroup {
    items: RelatedRecord[];
    total: number;
}

/** Bản ghi liên quan của một khách hàng (trang 360° Khách hàng). */
export interface CustomerRelatedResult {
    contacts: RelatedGroup;
    opportunities: RelatedGroup;
    quotations: RelatedGroup;
    orders: RelatedGroup;
    invoices: RelatedGroup;
    tickets: RelatedGroup;
    activities: RelatedGroup;
}

/** Bản ghi liên quan của một cơ hội (trang 360° Cơ hội). */
export interface OpportunityRelatedResult {
    quotations: RelatedGroup;
    orders: RelatedGroup;
    invoices: RelatedGroup;
    activities: RelatedGroup;
}
