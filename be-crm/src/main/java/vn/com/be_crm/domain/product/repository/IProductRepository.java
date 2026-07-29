package vn.com.be_crm.domain.product.repository;

import vn.com.be_crm.application.shared.dto.DeletedItemResult;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

/** Port lưu trữ cho Product. */
public interface IProductRepository {

    /** @param p entity cần lưu @return entity sau khi lưu */
    Product save(Product p);

    /** @param id ID hàng hóa @return Optional chứa Product (chưa xóa mềm) */
    Optional<Product> findById(Long id);

    /**
     * Tìm hàng hóa theo SKU (chưa xóa mềm) — dùng cho import UPDATE/BOTH.
     * @param sku mã SKU @return Optional
     */
    Optional<Product> findBySku(String sku);

    /**
     * Lấy toàn bộ hàng hóa thuộc 1 danh mục (chưa xóa mềm) — dùng để bulk-seed sản phẩm vào
     * chính sách giá khi chọn danh mục ({@code price_policy_product_categories}).
     * @param categoryId ID danh mục @return danh sách hàng hóa
     */
    List<Product> findAllByCategoryId(Long categoryId);

    /**
     * Xóa mềm hàng hóa theo ID, ghi nhận người xóa.
     * @param id        ID cần xóa
     * @param deletedBy ID người thực hiện xóa
     */
    void deleteById(Long id, Long deletedBy);

    /** @param r tham số phân trang @return PageResult (chỉ record chưa xóa mềm) */
    PageResult<Product> findAll(PageRequest r);

    /**
     * Lấy danh sách hàng hóa đã xóa mềm trong 30 ngày gần nhất.
     * @param userId  ID người dùng hiện tại
     * @param isAdmin true nếu admin
     * @param r       tham số phân trang
     */
    PageResult<DeletedItemResult> findDeleted(Long userId, boolean isAdmin, PageRequest r);

    /** Khôi phục hàng hóa từ thùng rác. @param id */
    void restoreById(Long id);

    /** Ẩn hàng hóa khỏi thùng rác. @param id */
    void purgeById(Long id);
}
