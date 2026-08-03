package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.application.product.mapper.ProductCategoryCommandMapper;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductCategoryRepository;
import vn.com.be_crm.core.error.frontend.NotFoundException;

/** Use case cập nhật danh mục hàng hóa. */
public class UpdateProductCategoryUseCase implements IUseCase<UpdateProductCategoryCommand, ProductCategoryResult> {
    private final IProductCategoryRepository repo;
    /** @param repo port lưu trữ */
    public UpdateProductCategoryUseCase(IProductCategoryRepository repo) { this.repo = repo; }
    /**
     * Cập nhật ProductCategory từ command.
     * @param cmd dữ liệu cập nhật @return ProductCategoryResult @throws NotFoundException
     */
    @Override
    public ProductCategoryResult execute(UpdateProductCategoryCommand cmd) {
        var e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("ProductCategory", cmd.getId()));
        return ProductCategoryCommandMapper.toResult(repo.save(ProductCategoryCommandMapper.toEntity(cmd, e)));
    }
}
