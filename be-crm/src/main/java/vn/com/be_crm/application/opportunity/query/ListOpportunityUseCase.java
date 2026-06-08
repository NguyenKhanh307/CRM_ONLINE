package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.opportunity.dto.OpportunityResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách cơ hội bán hàng có phân trang. */
public class ListOpportunityUseCase implements IUseCase<PageRequest, PageResult<OpportunityResult>> {
    private final IOpportunityRepository repo;
    /** @param repo port lưu trữ */
    public ListOpportunityUseCase(IOpportunityRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách Opportunity có phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<OpportunityResult> execute(PageRequest r) {
        var page = repo.findAll(r);
        return PageResult.<OpportunityResult>builder()
                .items(page.getItems().stream().map(OpportunityCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal()).page(page.getPage()).size(page.getSize()).build();
    }
}
