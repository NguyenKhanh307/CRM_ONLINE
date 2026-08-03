package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.opportunity.dto.OpportunityStageResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityStageCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityStageRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách giai đoạn pipeline có phân trang. */
public class ListOpportunityStageUseCase implements IUseCase<PageRequest, PageResult<OpportunityStageResult>> {
    private final IOpportunityStageRepository repo;
    /** @param repo port lưu trữ */
    public ListOpportunityStageUseCase(IOpportunityStageRepository repo) { this.repo = repo; }
    /** @param r request @return PageResult */
    @Override public PageResult<OpportunityStageResult> execute(PageRequest r) {
        var p = repo.findAll(r);
        return PageResult.<OpportunityStageResult>builder()
                .items(p.getItems().stream().map(OpportunityStageCommandMapper::toResult).collect(Collectors.toList()))
                .total(p.getTotal()).page(p.getPage()).size(p.getSize()).build();
    }
}
