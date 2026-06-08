package vn.com.be_crm.domain.product.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.product.entity.ProductCategory;

import java.util.Optional;

/** Port lưu trữ cho ProductCategory. */
public interface IProductCategoryRepository {
    /** @param c entity cần lưu @return entity sau khi lưu */
    ProductCategory save(ProductCategory c);
    /** @param id ID danh mục @return Optional chứa ProductCategory */
    Optional<ProductCategory> findById(Long id);
    /** @param id ID danh mục cần xóa */
    void deleteById(Long id);
    /** @param r tham số phân trang @return PageResult */
    PageResult<ProductCategory> findAll(PageRequest r);
}
