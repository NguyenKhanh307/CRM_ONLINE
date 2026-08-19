package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.CreateSlaPolicyCommand;
import vn.com.be_crm.application.service.dto.SlaPolicyResult;
import vn.com.be_crm.application.service.mapper.SlaPolicyMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;

/** Use case tạo mới chính sách SLA. */
public class CreateSlaPolicyUseCase implements IUseCase<CreateSlaPolicyCommand, SlaPolicyResult> {
    private final ISlaPolicyRepository repo;
    /** @param repo port lưu trữ */
    public CreateSlaPolicyUseCase(ISlaPolicyRepository repo) { this.repo = repo; }
    /** @param c command @return result */
    @Override public SlaPolicyResult execute(CreateSlaPolicyCommand c) {
        return SlaPolicyMapper.toResult(repo.save(SlaPolicyMapper.toEntity(c)));
    }
}
