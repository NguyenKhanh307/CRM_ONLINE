package vn.com.be_crm.application.quotation.mapper;

import vn.com.be_crm.application.quotation.dto.*;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.enums.QuotationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/** Chuyển đổi Command ↔ Quotation ↔ QuotationResult. */
public class QuotationCommandMapper {

    /**
     * Tạo Quotation từ CreateQuotationCommand.
     * @param cmd command tạo mới @return domain entity
     */
    public static Quotation toEntity(CreateQuotationCommand cmd) {
        return Quotation.builder()
                .code(cmd.getCode()).customerId(cmd.getCustomerId()).contactId(cmd.getContactId())
                .opportunityId(cmd.getOpportunityId()).pricePolicyId(cmd.getPricePolicyId())
                .ownerId(cmd.getOwnerId()).quoteDate(cmd.getQuoteDate()).validUntil(cmd.getValidUntil())
                .currency(cmd.getCurrency() != null ? cmd.getCurrency() : "VND")
                .exchangeRate(cmd.getExchangeRate() != null ? cmd.getExchangeRate() : BigDecimal.ONE)
                .status(cmd.getStatus() != null ? cmd.getStatus() : QuotationStatus.draft)
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : BigDecimal.ZERO)
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : BigDecimal.ZERO)
                .tax(cmd.getTax() != null ? cmd.getTax() : BigDecimal.ZERO)
                .total(cmd.getTotal() != null ? cmd.getTotal() : BigDecimal.ZERO)
                .note(cmd.getNote()).build();
    }

    /**
     * Cập nhật Quotation từ UpdateQuotationCommand.
     * @param cmd command cập nhật @param e entity hiện tại @return domain entity đã cập nhật
     */
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
                .currency(cmd.getCurrency() != null ? cmd.getCurrency() : e.getCurrency())
                .exchangeRate(cmd.getExchangeRate() != null ? cmd.getExchangeRate() : e.getExchangeRate())
                // Trạng thái KHÔNG nhận từ command — chỉ đổi qua hành động (submit/approve/reject/send).
                .status(e.getStatus())
                .subtotal(cmd.getSubtotal() != null ? cmd.getSubtotal() : e.getSubtotal())
                .discount(cmd.getDiscount() != null ? cmd.getDiscount() : e.getDiscount())
                .tax(cmd.getTax() != null ? cmd.getTax() : e.getTax())
                .total(cmd.getTotal() != null ? cmd.getTotal() : e.getTotal())
                .note(cmd.getNote() != null ? cmd.getNote() : e.getNote())
                .createdAt(e.getCreatedAt()).build();
    }

    /** Các trạng thái sẽ tự hiển thị "expired" khi quá ngày hiệu lực. */
    private static final Set<QuotationStatus> EXPIRABLE = Set.of(
            QuotationStatus.draft, QuotationStatus.pending, QuotationStatus.approved, QuotationStatus.sent);

    /**
     * Chuyển Quotation sang QuotationResult. Trạng thái "expired" được suy ra theo ngày hiệu lực
     * (không lưu DB) khi validUntil đã qua và báo giá chưa kết thúc bằng rejected.
     * @param e domain entity @return result DTO
     */
    public static QuotationResult toResult(Quotation e) {
        return QuotationResult.builder()
                .id(e.getId()).code(e.getCode()).customerId(e.getCustomerId()).contactId(e.getContactId())
                .opportunityId(e.getOpportunityId())
                .pricePolicyId(e.getPricePolicyId()).isPrimary(e.isPrimary()).isLocked(e.isLocked())
                .ownerId(e.getOwnerId()).quoteDate(e.getQuoteDate()).validUntil(e.getValidUntil())
                .currency(e.getCurrency()).exchangeRate(e.getExchangeRate())
                .status(effectiveStatus(e)).subtotal(e.getSubtotal()).discount(e.getDiscount())
                .tax(e.getTax()).total(e.getTotal()).note(e.getNote())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }

    /**
     * Suy ra trạng thái hiển thị: trả 'expired' nếu báo giá đã quá hạn hiệu lực.
     * @param e báo giá @return trạng thái hiệu lực
     */
    private static QuotationStatus effectiveStatus(Quotation e) {
        if (e.getValidUntil() != null && e.getValidUntil().isBefore(LocalDate.now())
                && EXPIRABLE.contains(e.getStatus())) {
            return QuotationStatus.expired;
        }
        return e.getStatus();
    }

    private QuotationCommandMapper() {}
}
