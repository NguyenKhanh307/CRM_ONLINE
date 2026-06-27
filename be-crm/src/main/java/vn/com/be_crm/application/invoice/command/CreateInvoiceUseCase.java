package vn.com.be_crm.application.invoice.command;

import vn.com.be_crm.application.invoice.dto.CreateInvoiceCommand;
import vn.com.be_crm.application.invoice.dto.InvoiceResult;
import vn.com.be_crm.application.invoice.mapper.InvoiceCommandMapper;
import vn.com.be_crm.application.invoice.mapper.InvoiceItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.invoice.repository.IInvoiceRepository;

import java.util.stream.Collectors;

/** Use case tạo mới đơn hàng (kèm dòng hàng nếu có). */
public class CreateInvoiceUseCase implements IUseCase<CreateInvoiceCommand, InvoiceResult> {
    private final IInvoiceRepository repo;
    /** @param repo port lưu trữ */
    public CreateInvoiceUseCase(IInvoiceRepository repo) { this.repo = repo; }
    /** Tạo mới Invoice; nếu có items thì lưu header + dòng hàng trong một transaction. @param cmd @return InvoiceResult */
    @Override public InvoiceResult execute(CreateInvoiceCommand cmd) {
        var entity = InvoiceCommandMapper.toEntity(cmd);
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            var items = cmd.getItems().stream()
                    .map(InvoiceItemCommandMapper::toEntity).collect(Collectors.toList());
            return InvoiceCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        return InvoiceCommandMapper.toResult(repo.save(entity));
    }
}
