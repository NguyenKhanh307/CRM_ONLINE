package vn.com.be_crm.application.auth.command;

import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IOrgUnitRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/**
 * Use case xóa đơn vị tổ chức.
 */
public class DeleteOrgUnitUseCase implements IUseCase<Long, Void> {

    private final IOrgUnitRepository repository;

    /**
     * @param repository port lưu trữ OrgUnit
     */
    public DeleteOrgUnitUseCase(IOrgUnitRepository repository) {
        this.repository = repository;
    }

    /**
     * Kiểm tra tồn tại rồi xóa OrgUnit theo ID.
     *
     * @param id ID đơn vị cần xóa
     * @return null
     * @throws NotFoundException nếu không tìm thấy OrgUnit
     */
    @Override
    public Void execute(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("OrgUnit", id));
        repository.deleteById(id);
        return null;
    }
}
