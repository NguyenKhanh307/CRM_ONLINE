package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.auth.dto.CreateOrgUnitCommand;
import vn.com.be_crm.application.auth.dto.OrgUnitResult;
import vn.com.be_crm.application.auth.mapper.OrgUnitCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.entity.OrgUnit;
import vn.com.be_crm.domain.auth.repository.IOrgUnitRepository;

/**
 * Use case tạo mới đơn vị tổ chức.
 */
public class CreateOrgUnitUseCase implements IUseCase<CreateOrgUnitCommand, OrgUnitResult> {

    private final IOrgUnitRepository repository;

    /**
     * @param repository port lưu trữ OrgUnit
     */
    public CreateOrgUnitUseCase(IOrgUnitRepository repository) {
        this.repository = repository;
    }

    /**
     * Tạo mới OrgUnit từ command, lưu và trả về result.
     *
     * @param command dữ liệu tạo mới
     * @return OrgUnitResult sau khi lưu
     */
    @Override
    public OrgUnitResult execute(CreateOrgUnitCommand command) {
        OrgUnit entity = OrgUnitCommandMapper.toEntity(command);
        OrgUnit saved = repository.save(entity);
        return OrgUnitCommandMapper.toResult(saved);
    }
}
