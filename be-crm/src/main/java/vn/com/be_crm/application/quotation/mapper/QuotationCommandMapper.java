package vn.com.be_crm.application.quotation.mapper;

import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.time.LocalDate;
import java.util.Set;

// chuyển đổi Command <-> Quotation <-> QuotationResult.
// subtotal/discount/tax/total KHÔNG còn trên Quotation domain entity — QuotationResult.toResult()
// ở đây để trống chúng, nơi gọi (Get/ListQuotationUseCase) tự tính từ dòng hàng rồi set vào sau.
public class QuotationCommandMapper {

    public static Quotation toEntity(CreateQuotationCommand cmd) {
        return Quotation.builder()
                .code(cmd.getCode()).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                .opportunityId(cmd.getOpportunityId())
                .pricePolicyId(cmd.getPricePolicyId())
                .ownerId(cmd.getOwnerId()).quoteDate(cmd.getQuoteDate()).validUntil(cmd.getValidUntil())
                .status(cmd.getStatus() != null ? cmd.getStatus() : QuotationStatus.draft)
                .note(cmd.getNote()).build();
    }

    public static Quotation toEntity(UpdateQuotationCommand cmd, Quotation e) {
        return Quotation.builder()
                .id(e.getId()).code(e.getCode())
                .customerId(cmd.getCustomerId() != null ? cmd.getCustomerId() : e.getCustomerId())
                .contactId(cmd.getContactId() != null ? cmd.getContactId() : e.getContactId())
                .opportunityId(cmd.getOpportunityId() != null ? cmd.getOpportunityId() : e.getOpportunityId())
                .pricePolicyId(cmd.getPricePolicyId() != null ? cmd.getPricePolicyId() : e.getPricePolicyId())
                .isPrimary(e.isPrimary()).isLocked(e.isLocked())
                .ownerId(cmd.getOwnerId() != null ? cmd.getOwnerId() : e.getOwnerId())
                .quoteDate(cmd.getQuoteDate() != null ? cmd.getQuoteDate() : e.getQuoteDate())
                .validUntil(cmd.getValidUntil() != null ? cmd.getValidUntil() : e.getValidUntil())
                // trạng thái KHÔNG nhận từ command — chỉ đổi qua hành động (submit/approve/reject/send)
                .status(e.getStatus())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .customerResponse(e.getCustomerResponse()).customerResponseNote(e.getCustomerResponseNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).build();
    }

    // các trạng thái sẽ tự hiển thị "expired" khi quá ngày hiệu lực
    private static final Set<QuotationStatus> EXPIRABLE = Set.of(
            QuotationStatus.draft, QuotationStatus.pending, QuotationStatus.approved, QuotationStatus.sent);

    // trạng thái "expired" được suy ra theo ngày hiệu lực (không lưu DB) khi validUntil đã qua và
    // báo giá chưa kết thúc bằng rejected. subtotal/discount/tax/total để trống — nơi gọi tự tính.
    public static QuotationResult toResult(Quotation e) {
        return QuotationResult.builder()
                .id(e.getId()).code(e.getCode()).customerId(e.getCustomerId()).contactId(e.getContactId())
                .opportunityId(e.getOpportunityId())
                .pricePolicyId(e.getPricePolicyId()).isPrimary(e.isPrimary()).isLocked(e.isLocked())
                .ownerId(e.getOwnerId()).quoteDate(e.getQuoteDate()).validUntil(e.getValidUntil())
                .status(effectiveStatus(e)).note(e.getNote())
                .customerResponse(e.getCustomerResponse()).customerResponseNote(e.getCustomerResponseNote())
                .createdBy(e.getCreatedBy()).updatedBy(e.getUpdatedBy())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    // suy ra trạng thái hiển thị: trả 'expired' nếu báo giá đã quá hạn hiệu lực
    private static QuotationStatus effectiveStatus(Quotation e) {
        if (e.getValidUntil() != null && e.getValidUntil().isBefore(LocalDate.now())
                && EXPIRABLE.contains(e.getStatus())) {
            return QuotationStatus.expired;
        }
        return e.getStatus();
    }

    private QuotationCommandMapper() {}
}
