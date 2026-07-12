package vn.com.be_crm.application.product.command;

import vn.com.be_crm.application.product.dto.*;
import vn.com.be_crm.application.product.mapper.ProductCommandMapper;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.application.shared.util.CrossFieldRules;
import vn.com.be_crm.domain.product.repository.IProductRepository;
import vn.com.be_crm.domain.shared.exception.NotFoundException;

/** Use case cập nhật hàng hóa. */
public class UpdateProductUseCase implements IUseCase<UpdateProductCommand, ProductResult> {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public UpdateProductUseCase(IProductRepository repo) { this.repo = repo; }
    /**
     * Cập nhật Product từ command.
     * @param cmd dữ liệu cập nhật @return ProductResult @throws NotFoundException
     */
    @Override
    public ProductResult execute(UpdateProductCommand cmd) {
        var e = repo.findById(cmd.getId()).orElseThrow(() -> new NotFoundException("Product", cmd.getId()));
        // Giá bán ≥ giá vốn — lấy giá trị mới nếu có, ngược lại giữ giá trị hiện tại
        CrossFieldRules.requireSellPriceNotBelowCost(
                cmd.getBasePrice() != null ? cmd.getBasePrice() : e.getBasePrice(),
                cmd.getCostPrice() != null ? cmd.getCostPrice() : e.getCostPrice());
        return ProductCommandMapper.toResult(repo.save(ProductCommandMapper.toEntity(cmd, e)));
    }
}
