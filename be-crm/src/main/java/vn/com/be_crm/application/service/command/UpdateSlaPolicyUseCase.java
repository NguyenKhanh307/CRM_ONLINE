package vn.com.be_crm.application.service.command;

import vn.com.be_crm.application.service.dto.SlaPolicyResult;
import vn.com.be_crm.application.service.dto.UpdateSlaPolicyCommand;
import vn.com.be_crm.application.service.mapper.SlaPolicyMapper;
import vn.com.be_crm.core.error.frontend.NotFoundException;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;

/** Use case cập nhật chính sách SLA. */
public class UpdateSlaPolicyUseCase implements IUseCase<UpdateSlaPolicyCommand, SlaPolicyResult> {
    private final ISlaPolicyRepository repo;
    /** @param repo port lưu trữ */
    public UpdateSlaPolicyUseCase(ISlaPolicyRepository repo) { this.repo = repo; }
    /** @param c command @return result @throws NotFoundException */
    @Override public SlaPolicyResult execute(UpdateSlaPolicyCommand c) {
        var e = repo.findById(c.getId()).orElseThrow(() -> new NotFoundException("SlaPolicy", c.getId()));
        return SlaPolicyMapper.toResult(repo.save(SlaPolicyMapper.toEntity(c, e)));
    }
}
