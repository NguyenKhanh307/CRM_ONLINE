package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductCategoryRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/**
 * Use case xóa danh mục sản phẩm trong chính sách giá — chỉ xóa dòng marker
 * ({@code price_policy_product_categories}), KHÔNG cascade xóa các dòng {@code price_policy_products}
 * đã bulk-seed lúc thêm danh mục (đã trở thành dòng giá độc lập, có thể đã bị chỉnh tay).
 */
public class DeletePricePolicyProductCategoryUseCase implements IUseCase<Long, Void> {
    private final IPricePolicyProductCategoryRepository repo;
    /** @param repo port lưu trữ */
    public DeletePricePolicyProductCategoryUseCase(IPricePolicyProductCategoryRepository repo) { this.repo = repo; }
    /** Xóa PricePolicyProductCategory. @param id @return null @throws NotFoundException */
    @Override public Void execute(Long id) {
        repo.findById(id).orElseThrow(() -> new NotFoundException("PricePolicyProductCategory not found: " + id));
        repo.deleteById(id); return null;
    }
}
