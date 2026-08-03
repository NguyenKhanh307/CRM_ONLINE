package vn.com.be_crm.application.product.query;

import vn.com.be_crm.application.product.dto.ProductResult;
import vn.com.be_crm.application.product.mapper.ProductCommandMapper;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.core.page.PageResult;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductRepository;

import java.util.List;
import java.util.stream.Collectors;

/** Use case lấy danh sách hàng hóa có phân trang. */
public class ListProductUseCase implements IUseCase<PageRequest, PageResult<ProductResult>> {
    private final IProductRepository repo;
    private final INameResolver names;
    /** @param repo port lưu trữ @param names port tra tên khóa ngoại */
    public ListProductUseCase(IProductRepository repo, INameResolver names) { this.repo = repo; this.names = names; }
    /**
     * Lấy danh sách Product theo tham số phân trang, kèm tên danh mục.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<ProductResult> execute(PageRequest r) {
        var p = repo.findAll(r);
        List<ProductResult> items = p.getItems().stream().map(ProductCommandMapper::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, ProductResult::getCategoryId, names::productCategories, ProductResult::setCategoryName);
        NameEnricher.apply(items, ProductResult::getCreatedBy, names::users, ProductResult::setCreatedByName);
        NameEnricher.apply(items, ProductResult::getUpdatedBy, names::users, ProductResult::setUpdatedByName);
        return PageResult.<ProductResult>builder()
                .items(items)
                .total(p.getTotal()).page(p.getPage()).size(p.getSize()).build();
    }
}
