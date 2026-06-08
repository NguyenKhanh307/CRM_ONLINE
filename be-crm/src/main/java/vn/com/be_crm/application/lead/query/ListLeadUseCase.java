package vn.com.be_crm.application.lead.query;

import vn.com.be_crm.application.lead.dto.LeadResult;
import vn.com.be_crm.application.lead.mapper.LeadCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.lead.repository.ILeadRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách tiềm năng có phân trang. */
public class ListLeadUseCase implements IUseCase<PageRequest, PageResult<LeadResult>> {
    private final ILeadRepository repo;
    /** @param repo port lưu trữ */
    public ListLeadUseCase(ILeadRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách Lead có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<LeadResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<LeadResult>builder()
                .items(page.getItems().stream().map(LeadCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
