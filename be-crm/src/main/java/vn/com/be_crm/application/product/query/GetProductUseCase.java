package vn.com.be_crm.application.product.query;

import vn.com.be_crm.application.product.dto.ProductResult;
import vn.com.be_crm.application.product.mapper.ProductCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case lấy hàng hóa theo ID. */
public class GetProductUseCase implements IUseCase<Long, ProductResult> {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public GetProductUseCase(IProductRepository repo) { this.repo = repo; }
    /**
     * Tìm Product theo ID.
     * @param id ID hàng hóa @return ProductResult @throws NotFoundException
     */
    @Override
    public ProductResult execute(Long id) {
        return repo.findById(id).map(ProductCommandMapper::toResult)
                .orElseThrow(() -> new NotFoundException("Product", id));
    }
}
