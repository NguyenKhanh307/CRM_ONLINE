package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.application.product.mapper.ProductCategoryCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductCategoryRepository;

/** Use case tạo mới danh mục hàng hóa. */
public class CreateProductCategoryUseCase implements IUseCase<CreateProductCategoryCommand, ProductCategoryResult> {
    private final IProductCategoryRepository repo;
    /** @param repo port lưu trữ */
    public CreateProductCategoryUseCase(IProductCategoryRepository repo) { this.repo = repo; }
    /**
     * Tạo mới ProductCategory và trả về result.
     * @param cmd dữ liệu tạo mới @return ProductCategoryResult
     */
    @Override
    public ProductCategoryResult execute(CreateProductCategoryCommand cmd) {
        return ProductCategoryCommandMapper.toResult(repo.save(ProductCategoryCommandMapper.toEntity(cmd)));
    }
}
