package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.shared.util.CrossFieldRules;
import vn.com.be_crm.application.invoice.dto.CreateInvoiceCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.shared.exception.DomainException;
import vn.com.be_crm.application.shared.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** Use case tạo mới hóa đơn (kèm dòng hàng nếu có). */
public class CreateInvoiceUseCase implements IUseCase<CreateInvoiceCommand, InvoiceResult> {
    private final IInvoiceRepository repo;
    /** @param repo port lưu trữ */
    public CreateInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }

    /**
     * Tạo mới Invoice; nếu có items thì lưu header + dòng hàng trong một transaction.
     * Thành tiền từng dòng và tổng tiền chứng từ do SERVER tính lại — bỏ qua số tiền client gửi lên.
     * @param cmd @return InvoiceResult
     */
    @Override public InvoiceResult execute(CreateInvoiceCommand cmd) {
        // Ràng buộc khoảng thời gian: hạn thanh toán không được trước ngày hóa đơn
        CrossFieldRules.requireDateRange(cmd.getInvoiceDate(), cmd.getDueDate(), "Ngày hóa đơn", "Hạn thanh toán");
        // Check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã hóa đơn \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        var entity = InvoiceCommandMapper.toEntity(cmd);
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            List<InvoiceItem> items = cmd.getItems().stream()
                    .map(InvoiceItemCommandMapper::toEntity)
                    .map(CreateInvoiceUseCase::withComputedAmount)
                    .collect(Collectors.toList());
            var totals = LineItemTotals.compute(items, InvoiceItem::getQuantity, InvoiceItem::getUnitPrice,
                    InvoiceItem::getDiscount, InvoiceItem::getTaxRate);
            entity = entity.toBuilder()
                    .subtotal(totals.subtotal()).discount(totals.discount())
                    .tax(totals.tax()).total(totals.total())
                    .build();
            return InvoiceCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        entity = entity.toBuilder()
                .subtotal(BigDecimal.ZERO).discount(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO).total(BigDecimal.ZERO)
                .build();
        return InvoiceCommandMapper.toResult(repo.save(entity));
    }

    /** Ghi đè thành tiền của dòng hàng bằng giá trị server tính. */
    private static InvoiceItem withComputedAmount(InvoiceItem i) {
        return i.toBuilder()
                .amount(LineItemTotals.lineAmount(i.getQuantity(), i.getUnitPrice(), i.getDiscount(), i.getTaxRate()))
                .build();
    }
}
