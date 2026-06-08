package vn.com.be_crm.application.opportunity.query;

import vn.com.be_crm.application.opportunity.dto.OpportunityItemResult;
import vn.com.be_crm.application.opportunity.mapper.OpportunityItemCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.opportunity.repository.IOpportunityItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách dòng sản phẩm theo opportunityId. */
public class ListOpportunityItemUseCase implements IUseCase<Long, List<OpportunityItemResult>> {
    private final IOpportunityItemRepository repo;
    /** @param repo port lưu trữ */
    public ListOpportunityItemUseCase(IOpportunityItemRepository repo) { this.repo = repo; }

    /**
     * Lấy danh sách OpportunityItem theo opportunityId.
     * @param opportunityId ID cơ hội @return danh sách result
     */
    @Override
    public List<OpportunityItemResult> execute(Long opportunityId) {
        return repo.findAllByOpportunityId(opportunityId).stream()
                .map(OpportunityItemCommandMapper::toResult).collect(Collectors.toList());
    }
}
