package vn.com.be_crm.application.quotation.query;

import vn.com.be_crm.application.quotation.dto.QuotationResult;
import vn.com.be_crm.application.quotation.mapper.QuotationCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.quotation.repository.IQuotationRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách báo giá có phân trang. */
public class ListQuotationUseCase implements IUseCase<PageRequest, PageResult<QuotationResult>> {
    private final IQuotationRepository repo;
    /** @param repo port lưu trữ */
    public ListQuotationUseCase(IQuotationRepository repo) { this.repo = repo; }
    /** Lấy danh sách Quotation. @param r phân trang @return PageResult */
    @Override public PageResult<QuotationResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<QuotationResult>builder()
                .items(page.getItems().stream().map(QuotationCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
