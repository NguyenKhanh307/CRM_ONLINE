package vn.com.be_crm.application.auth.query;

import vn.com.be_crm.application.auth.dto.OrgUnitResult;
import vn.com.be_crm.application.auth.mapper.OrgUnitCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.auth.repository.IOrgUnitRepository;

import java.util.stream.Collectors;

/**
 * Use case lấy danh sách đơn vị tổ chức có phân trang.
 */
public class ListOrgUnitUseCase implements IUseCase<PageRequest, PageResult<OrgUnitResult>> {

    private final IOrgUnitRepository repository;

    /**
     * @param repository port lưu trữ OrgUnit
     */
    public ListOrgUnitUseCase(IOrgUnitRepository repository) {
        this.repository = repository;
    }

    /**
     * Lấy danh sách OrgUnit theo tham số phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách OrgUnitResult
     */
    @Override
    public PageResult<OrgUnitResult> execute(PageRequest request) {
        PageResult<vn.com.be_crm.domain.auth.entity.OrgUnit> page = repository.findAll(request);
        return PageResult.<OrgUnitResult>builder()
                .items(page.getItems().stream().map(OrgUnitCommandMapper::toResult).collect(Collectors.toList()))
                .total(page.getTotal())
                .page(page.getPage())
                .size(page.getSize())
                .build();
    }
}
