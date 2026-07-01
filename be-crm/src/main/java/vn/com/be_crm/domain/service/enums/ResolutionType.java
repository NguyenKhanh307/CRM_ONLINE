package vn.com.be_crm.domain.service.enums;

/**
 * Hình thức giải quyết yêu cầu sau bán.
 * Ranh giới CRM: chỉ ghi nhận hình thức, không tạo phiếu kho/hoàn tiền thật (ERP).
 */
public enum ResolutionType {
    refund, replacement, repair, store_credit, answered, rejected
}
