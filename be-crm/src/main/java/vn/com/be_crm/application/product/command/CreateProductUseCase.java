package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.application.product.mapper.ProductCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductRepository;

/** Use case tạo mới hàng hóa. */
public class CreateProductUseCase implements IUseCase<CreateProductCommand, ProductResult> {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public CreateProductUseCase(IProductRepository repo) { this.repo = repo; }
    /**
     * Tạo mới Product và trả về result.
     * @param cmd dữ liệu tạo mới @return ProductResult
     */
    @Override
    public ProductResult execute(CreateProductCommand cmd) {
        return ProductCommandMapper.toResult(repo.save(ProductCommandMapper.toEntity(cmd)));
    }
}
