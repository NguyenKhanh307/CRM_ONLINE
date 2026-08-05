package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.core.util.CrossFieldRules;
import vn.com.be_crm.application.quotation.dto.CreateQuotationCommand;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.core.error.frontend.DomainException;
import vn.com.be_crm.core.util.LineItemTotals;
import vn.com.be_crm.domain.quotation.entity.Quotation;
import vn.com.be_crm.domain.quotation.entity.QuotationItem;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.util.List;
import java.util.stream.Collectors;

// tạo mới báo giá (kèm dòng hàng nếu có). Thành tiền từng dòng và tổng tiền chứng từ do SERVER
// tính (SL x đơn giá - CK + thuế) tại thời điểm đọc — bỏ qua mọi số tiền client gửi lên, không
// còn lưu vào cột nào trên Quotation/QuotationItem.
public class CreateQuotationUseCase implements IUseCase<CreateQuotationCommand, QuotationResult> {
    private final IQuotationRepository repo;

    public CreateQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }

    @Override public QuotationResult execute(CreateQuotationCommand cmd) {
        // ràng buộc khoảng thời gian: ngày hiệu lực không được trước ngày báo giá
        CrossFieldRules.requireDateRange(cmd.getQuoteDate(), cmd.getValidUntil(), "Ngày báo giá", "Ngày hiệu lực");
        // check trùng thân thiện trước khi insert (DB unique constraint là lớp phòng thủ 2)
        if (cmd.getCode() != null && repo.findByCode(cmd.getCode()).isPresent()) {
            throw new DomainException("Mã báo giá \"" + cmd.getCode() + "\" đã tồn tại, vui lòng dùng mã khác");
        }
        var entity = QuotationCommandMapper.toEntity(cmd);
        List<QuotationItem> items = cmd.getItems() != null
                ? cmd.getItems().stream().map(QuotationItemCommandMapper::toEntity).collect(Collectors.toList())
                : List.of();
        Quotation saved = items.isEmpty() ? repo.save(entity) : repo.saveWithItems(entity, items);

        QuotationResult result = QuotationCommandMapper.toResult(saved);
        LineItemTotals.Totals t = LineItemTotals.compute(items,
                QuotationItem::getQuantity, QuotationItem::getUnitPrice, QuotationItem::getDiscount, QuotationItem::getTaxRate);
        result.setSubtotal(t.subtotal()); result.setDiscount(t.discount());
        result.setTax(t.tax()); result.setTotal(t.total());
        return result;
    }
}
