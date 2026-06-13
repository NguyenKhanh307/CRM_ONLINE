package vn.com.be_crm.application.quotation.command;

import vn.com.be_crm.application.quotation.dto.CreateQuotationCommand;
import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.quotation.mapper.QuotationItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.util.stream.Collectors;

/** Use case tạo mới báo giá (kèm dòng hàng nếu có). */
public class CreateQuotationUseCase implements IUseCase<CreateQuotationCommand, QuotationResult> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public CreateQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Tạo mới Quotation; nếu có items thì lưu header + dòng hàng trong một transaction. @param cmd @return QuotationResult */
    @Override public QuotationResult execute(CreateQuotationCommand cmd) {
        var entity = QuotationCommandMapper.toEntity(cmd);
        if (cmd.getItems() != null && !cmd.getItems().isEmpty()) {
            var items = cmd.getItems().stream()
                    .map(QuotationItemCommandMapper::toEntity).collect(Collectors.toList());
            return QuotationCommandMapper.toResult(repo.saveWithItems(entity, items));
        }
        return QuotationCommandMapper.toResult(repo.save(entity));
    }
}
