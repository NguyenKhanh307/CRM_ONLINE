package vn.com.be_crm.application.service.query;

import vn.com.be_crm.application.service.dto.SlaPolicyResult;
import vn.com.be_crm.application.service.mapper.SlaPolicyMapper;
import vn.com.be_crm.domain.service.repository.ISlaPolicyRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy toàn bộ chính sách SLA. */
public class ListSlaPolicyUseCase {
    private final ISlaPolicyRepository repo;
    /** @param repo port lưu trữ */
    public ListSlaPolicyUseCase(ISlaPolicyRepository repo) { this.repo = repo; }
    /** Lấy danh sách SlaPolicy. @return danh sách */
    public List<SlaPolicyResult> execute() {
        return repo.findAll().stream().map(SlaPolicyMapper::toResult).collect(Collectors.toList());
    }
}
