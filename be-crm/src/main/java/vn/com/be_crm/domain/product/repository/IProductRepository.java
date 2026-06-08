package vn.com.be_crm.domain.product.repository;

import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.product.entity.Product;

import java.util.Optional;

/** Port lưu trữ cho Product. */
public interface IProductRepository {
    /** @param p entity cần lưu @return entity sau khi lưu */
    Product save(Product p);
    /** @param id ID hàng hóa @return Optional chứa Product (chưa xóa mềm) */
    Optional<Product> findById(Long id);
    /** Xóa mềm hàng hóa. @param id ID cần xóa */
    void deleteById(Long id);
    /** @param r tham số phân trang @return PageResult (chỉ record chưa xóa mềm) */
    PageResult<Product> findAll(PageRequest r);
}
