package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.OrgUnitResult;
import vn.com.be_crm.application.auth.dto.UpdateOrgUnitCommand;
import vn.com.be_crm.application.auth.mapper.OrgUnitCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.OrgUnit;
import vn.com.be_crm.domain.auth.repository.IOrgUnitRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case cập nhật đơn vị tổ chức.
 */
public class UpdateOrgUnitUseCase implements IUseCase<UpdateOrgUnitCommand, OrgUnitResult> {

    private final IOrgUnitRepository repository;

    /**
     * @param repository port lưu trữ OrgUnit
     */
    public UpdateOrgUnitUseCase(IOrgUnitRepository repository) {
        this.repository = repository;
    }

    /**
     * Tìm OrgUnit theo ID, cập nhật từ command, lưu và trả về result.
     *
     * @param command dữ liệu cập nhật (bao gồm id)
     * @return OrgUnitResult sau khi cập nhật
     * @throws NotFoundException nếu không tìm thấy OrgUnit
     */
    @Override
    public OrgUnitResult execute(UpdateOrgUnitCommand command) {
        OrgUnit existing = repository.findById(command.getId())
                .orElseThrow(() -> new NotFoundException("OrgUnit", command.getId()));
        OrgUnit updated = OrgUnitCommandMapper.toEntity(command, existing);
        OrgUnit saved = repository.save(updated);
        return OrgUnitCommandMapper.toResult(saved);
    }
}
