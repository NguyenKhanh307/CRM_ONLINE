package vn.com.be_crm.domain.auth.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.auth.entity.OrgUnit;

import java.util.Optional;

/**
 * Port lưu trữ cho OrgUnit — chỉ domain interface, không biết Hibernate.
 */
public interface IOrgUnitRepository {

    /**
     * Lưu mới hoặc cập nhật đơn vị tổ chức.
     *
     * @param orgUnit entity cần lưu
     * @return entity sau khi lưu (có ID nếu mới)
     */
    OrgUnit save(OrgUnit orgUnit);

    /**
     * Tìm đơn vị theo ID.
     *
     * @param id ID đơn vị
     * @return Optional chứa OrgUnit nếu tìm thấy
     */
    Optional<OrgUnit> findById(Long id);

    /**
     * Xóa đơn vị theo ID.
     *
     * @param id ID đơn vị cần xóa
     */
    void deleteById(Long id);

    /**
     * Lấy danh sách đơn vị có phân trang.
     *
     * @param request tham số phân trang
     * @return PageResult chứa danh sách OrgUnit
     */
    PageResult<OrgUnit> findAll(PageRequest request);
}
