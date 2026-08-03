package vn.com.be_crm.application.product.query;

import vn.com.be_crm.application.product.dto.ProductCategoryResult;
import vn.com.be_crm.application.product.mapper.ProductCategoryCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductCategoryRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case lấy danh mục hàng hóa theo ID. */
public class GetProductCategoryUseCase implements IUseCase<Long, ProductCategoryResult> {
    private final IProductCategoryRepository repo;
    /** @param repo port lưu trữ */
    public GetProductCategoryUseCase(IProductCategoryRepository repo) { this.repo = repo; }
    /**
     * Tìm ProductCategory theo ID.
     * @param id ID danh mục @return ProductCategoryResult @throws NotFoundException
     */
    @Override
    public ProductCategoryResult execute(Long id) {
        return repo.findById(id).map(ProductCategoryCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("ProductCategory", id));
    }
}
