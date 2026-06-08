package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.OrgUnitResult;
import vn.com.be_crm.application.auth.mapper.OrgUnitCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IOrgUnitRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case lấy thông tin đơn vị tổ chức theo ID.
 */
public class GetOrgUnitUseCase implements IUseCase<Long, OrgUnitResult> {

    private final IOrgUnitRepository repository;

    /**
     * @param repository port lưu trữ OrgUnit
     */
    public GetOrgUnitUseCase(IOrgUnitRepository repository) {
        this.repository = repository;
    }

    /**
     * Tìm OrgUnit theo ID và trả về result.
     *
     * @param id ID đơn vị
     * @return OrgUnitResult
     * @throws NotFoundException nếu không tìm thấy
     */
    @Override
    public OrgUnitResult execute(Long id) {
        return repository.findById(id)
                .map(OrgUnitCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("OrgUnit", id));
    }
}
