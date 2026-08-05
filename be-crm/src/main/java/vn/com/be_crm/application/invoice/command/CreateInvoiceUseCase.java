package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.invoice.dto.CreateInvoiceCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.invoice.entity.Invoice;
import vn.com.be_crm.domain.invoice.entity.InvoiceItem;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.util.List;
import java.util.stream.Collectors;

// tạo mới hóa đơn (kèm dòng hàng nếu có). Thành tiền từng dòng và tổng tiền chứng từ do SERVER
// tính tại thời điểm đọc — bỏ qua số tiền client gửi lên, không lưu vào cột nào.
public class CreateInvoiceUseCase implements IUseCase<CreateInvoiceCommand, InvoiceResult> {
    private final IInvoiceRepository repo;

    public CreateInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }

    @Override public InvoiceResult execute(CreateInvoiceCommand cmd) {
        // ràng buộc khoảng thời gian: hạn thanh toán không được trước ngày hóa đơn
        CrossFieldRules.requireDateRange(cmd.getInvoiceDate(), cmd.getDueDate(), "Ngày hóa đơn", "Hạn thanh toán");
        // check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã hóa đơn \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        var entity = InvoiceCommandMapper.toEntity(cmd);
        List<InvoiceItem> items = cmd.getItems() != null
                ? cmd.getItems().stream().map(InvoiceItemCommandMapper::toEntity).collect(Collectors.toList())
                : List.of();
        Invoice saved = items.isEmpty() ? repo.save(entity) : repo.saveWithItems(entity, items);

        InvoiceResult result = InvoiceCommandMapper.toResult(saved);
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                InvoiceItem::getQuantity, InvoiceItem::getUnitPrice, InvoiceItem::getDiscount, InvoiceItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());
        return result;
    }
}
