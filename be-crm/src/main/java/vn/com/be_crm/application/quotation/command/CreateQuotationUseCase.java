package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.shared.util.CrossFieldRules;
import vn.com.be_crm.application.quotation.dto.CreateQuotationCommand;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** Use case tạo mới báo giá (kèm dòng hàng nếu có). */
public class CreateQuotationUseCase implements IUseCase<CreateQuotationCommand, QuotationResult> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public CreateQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Quotation; nếu có items thì lưu header + dòng hàng trong một transaction.
     * Thành tiền từng dòng và tổng tiền chứng từ do SERVER tính lại từ (SL × đơn giá − CK + thuế) —
     * bỏ qua mọi số tiền client gửi lên.
     * @param cmd @return QuotationResult
     */
    @Override public QuotationResult execute(CreateQuotationCommand cmd) {
        // Ràng buộc khoảng thời gian: ngày hiệu lực không được trước ngày báo giá
        CrossFieldRules.requireDateRange(cmd.getQuoteDate(), cmd.getValidUntil(), "Ngày báo giá", "Ngày hiệu lực");
        // Check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã báo giá \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        var entity = QuotationCommandMapper.toEntity(cmd);
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            List<QuotationItem> items = cmd.getItems().stream()
                    .map(QuotationItemCommandMapper::toEntity)
                    .map(CreateQuotationUseCase::withComputedAmount)
                    .collect(Collectors.toList());
            var totals = LineItemTotals.compute(items, QuotationItem::getQuantity, QuotationItem::getUnitPrice,
                    QuotationItem::getDiscount, QuotationItem::getTaxRate);
            entity = entity.toBuilder()
                    .subtotal(totals.subtotal()).discount(totals.discount())
                    .tax(totals.tax()).total(totals.total())
                    .build();
            return QuotationCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        // Không có dòng hàng → tổng tiền bằng 0 (không nhận tổng thủ công từ client)
        entity = entity.toBuilder()
                .subtotal(BigDecimal.ZERO).discount(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO).total(BigDecimal.ZERO)
                .build();
        return QuotationCommandMapper.toResult(repo.save(entity));
    }

    /** Ghi đè thành tiền của dòng hàng bằng giá trị server tính. */
    private static QuotationItem withComputedAmount(QuotationItem i) {
        return i.toBuilder()
                .amount(LineItemTotals.lineAmount(i.getQuantity(), i.getUnitPrice(), i.getDiscount(), i.getTaxRate()))
                .build();
    }
}
