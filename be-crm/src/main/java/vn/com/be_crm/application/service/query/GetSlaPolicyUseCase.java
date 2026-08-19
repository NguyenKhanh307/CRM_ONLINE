package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.SlaPolicyResult;
import vn.com.be_crm.application.service.mapper.SlaPolicyMapper;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;

/** Use case lấy chính sách SLA theo ID. */
public class GetSlaPolicyUseCase implements IUseCase<Long, SlaPolicyResult> {
    private final ISlaPolicyRepository repo;
    /** @param repo port lưu trữ */
    public GetSlaPolicyUseCase(ISlaPolicyRepository repo) { this.repo = repo; }
    /** @param id ID @return result @throws NotFoundException */
    @Override public SlaPolicyResult execute(Long id) {
        return repo.findById(id).map(SlaPolicyMapper::toResult)
                .orElseThrow(() -> new NotFoundException("SlaPolicy", id));
    }
}
