// phân hệ của một bản ghi liên quan — dùng để deep-link sang trang tương ứng
export type RelatedModule =
    | 'lead'
    | 'contact'
    | 'customer'
    | 'opportunity'
    | 'quotation'
    | 'order'
    | 'invoice'
    | 'ticket'
    | 'activity';

// một dòng bản ghi liên quan trên trang chi tiết 360°
// shape chung cho mọi phân hệ — mỗi tab tự chọn hiển thị cột nào (xem RelatedTable)
export interface RelatedRecord {
    module: RelatedModule;
    id: number;
    // mã bản ghi, hoặc giá trị phụ: email (liên hệ), loại hoạt động (call/meeting...)
    code: string | null;
    name: string | null;
    // trạng thái, hoặc nhãn phụ: chức danh (liên hệ)
    status: string | null;
    // ISO datetime từ BE, vd "2026-07-12T10:00:00"
    date: string | null;
    amount: number | null;
    ownerName: string | null;
}

// nhóm bản ghi liên quan: danh sách rút gọn (tối đa 50 dòng) + tổng số thật trong DB
export interface RelatedGroup {
    items: RelatedRecord[];
    total: number;
}

// bản ghi liên quan của một khách hàng (trang 360° Khách hàng)
export interface CustomerRelatedResult {
    contacts: RelatedGroup;
    opportunities: RelatedGroup;
    quotations: RelatedGroup;
    orders: RelatedGroup;
    invoices: RelatedGroup;
    tickets: RelatedGroup;
    activities: RelatedGroup;
}

// bản ghi liên quan của một cơ hội (trang 360° Cơ hội)
export interface OpportunityRelatedResult {
    quotations: RelatedGroup;
    orders: RelatedGroup;
    invoices: RelatedGroup;
    activities: RelatedGroup;
}

// bản ghi liên quan của một tiềm năng (trang chi tiết Tiềm năng)
export interface LeadRelatedResult {
    opportunities: RelatedGroup;
    activities: RelatedGroup;
}

// bản ghi liên quan của một liên hệ (trang chi tiết Liên hệ)
export interface ContactRelatedResult {
    opportunities: RelatedGroup;
    quotations: RelatedGroup;
    orders: RelatedGroup;
    invoices: RelatedGroup;
    tickets: RelatedGroup;
    activities: RelatedGroup;
}

// bản ghi liên quan của một báo giá (trang chi tiết Báo giá)
export interface QuotationRelatedResult {
    orders: RelatedGroup;
    invoices: RelatedGroup;
    activities: RelatedGroup;
}

// bản ghi liên quan của một đơn hàng (trang chi tiết Đơn hàng)
export interface OrderRelatedResult {
    invoices: RelatedGroup;
    activities: RelatedGroup;
}

// bản ghi liên quan của một hóa đơn (trang chi tiết Hóa đơn)
export interface InvoiceRelatedResult {
    tickets: RelatedGroup;
    activities: RelatedGroup;
}

// bản ghi quy về một chiến dịch (trang chi tiết Chiến dịch)
// chiều đọc ngược của attribution: campaign_id gắn khi tạo/convert ở từng phân hệ
export interface CampaignRelatedResult {
    leads: RelatedGroup;
    opportunities: RelatedGroup;
    orders: RelatedGroup;
    invoices: RelatedGroup;
}
