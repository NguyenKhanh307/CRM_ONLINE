package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.enums.InvoiceStatus;
import vn.com.be_crm.domain.invoice.enums.PaymentStatus;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;
import vn.com.be_crm.domain.opportunity.entity.Opportunity;
import vn.com.be_crm.domain.opportunity.enums.OpportunityStatus;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationItemRepository;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case chuyển báo giá thành hóa đơn (Quote-to-Invoice):
 * sao chép sâu dòng hàng QLI → InvoiceLineItem, khóa báo giá (audit trail),
 * và chuyển cơ hội liên quan sang Chốt Thắng (won).
 */
public class ConvertQuotationToInvoiceUseCase {
    private final IQuotationRepository quotationRepo;
    private final IQuotationItemRepository quotationItemRepo;
    private final IInvoiceRepository invoiceRepo;
    private final IOpportunityRepository opportunityRepo;

    /** @param quotationRepo báo giá @param quotationItemRepo dòng báo giá @param invoiceRepo hóa đơn @param opportunityRepo cơ hội */
    public ConvertQuotationToInvoiceUseCase(IQuotationRepository quotationRepo, IQuotationItemRepository quotationItemRepo,
                                            IInvoiceRepository invoiceRepo, IOpportunityRepository opportunityRepo) {
        this.quotationRepo = quotationRepo;
        this.quotationItemRepo = quotationItemRepo;
        this.invoiceRepo = invoiceRepo;
        this.opportunityRepo = opportunityRepo;
    }

    /**
     * Chuyển báo giá thành hóa đơn.
     * @param quotationId ID báo giá @return hóa đơn vừa tạo
     */
    public InvoiceResult execute(Long quotationId) {
        Quotation q = quotationRepo.findById(quotationId)
                .orElseThrow(() -> new NotFoundException("Quotation not found: " + quotationId));
        if (q.isLocked()) throw new DomainException("Báo giá đã được chuyển thành hóa đơn trước đó");

        // Sao chép dòng hàng báo giá → dòng hàng hóa đơn
        List<QuotationItem> qItems = quotationItemRepo.findAllByQuotationId(quotationId);
        List<InvoiceItem> invItems = qItems.stream().map(qi -> InvoiceItem.builder()
                .productId(qi.getProductId()).unit(qi.getUnit())
                .quantity(qi.getQuantity()).unitPrice(qi.getUnitPrice()).discount(qi.getDiscount())
                .taxRate(qi.getTaxRate()).amount(qi.getAmount()).note(qi.getNote())
                .build()).collect(Collectors.toList());

        Invoice invoice = Invoice.builder()
                .code("HD-" + System.currentTimeMillis())
                .customerId(q.getCustomerId()).contactId(q.getContactId())
                .quotationId(q.getId()).opportunityId(q.getOpportunityId())
                .ownerId(q.getOwnerId())
                .currency(q.getCurrency()).exchangeRate(q.getExchangeRate())
                .status(InvoiceStatus.draft).paymentStatus(PaymentStatus.unpaid)
                .subtotal(q.getSubtotal()).discount(q.getDiscount()).tax(q.getTax()).total(q.getTotal())
                .build();
        Invoice savedInvoice = invoiceRepo.saveWithItems(invoice, invItems);

        // Khóa báo giá (read-only — dấu vết kiểm toán)
        quotationRepo.save(q.toBuilder().isLocked(true).build());

        // Chuyển cơ hội liên quan sang Chốt Thắng (won)
        if (q.getOpportunityId() != null) {
            Opportunity opp = opportunityRepo.findById(q.getOpportunityId()).orElse(null);
            if (opp != null && opp.getStatus() != OpportunityStatus.won) {
                opportunityRepo.save(opp.toBuilder().status(OpportunityStatus.won).build());
            }
        }
        return InvoiceCommandMapper.toResult(savedInvoice);
    }
}
