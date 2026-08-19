package vn.com.be_crm.application.product.query;

import vn.com.be_crm.application.product.dto.PublicProductResult;
import vn.com.be_crm.core.lookup.NameEnricher;
import vn.com.be_crm.core.lookup.port.INameResolver;
import vn.com.be_crm.core.page.PageRequest;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;

import java.util.List;
import java.util.stream.Collectors;

// danh sách hàng hóa/dịch vụ đang hoạt động cho landing page công khai (/api/tracking/products)
// — trả DTO rút gọn, không lộ costPrice hay field nội bộ
public class ListPublicProductsUseCase {
    private final IProductRepository repo;
    private final INameResolver names;

    public ListPublicProductsUseCase(IProductRepository repo, INameResolver names) {
        this.repo = repo;
        this.names = names;
    }

    // hàm thực thi use case, trả danh sách DTO rút gọn (không lộ costPrice hay
    // field nội bộ)
    public List<PublicProductResult> execute() {
        var page = repo.findAll(PageRequest.builder()
                .size(1000).sortBy("name").sortDir("asc").status("true").build());
        List<PublicProductResult> items = page.getItems().stream().map(this::toResult).collect(Collectors.toList());
        NameEnricher.apply(items, PublicProductResult::getCategoryId, names::productCategories,
                PublicProductResult::setCategoryName);
        return items;
    }

    private PublicProductResult toResult(Product p) {
        return PublicProductResult.builder()
                .id(p.getId()).sku(p.getSku()).name(p.getName()).categoryId(p.getCategoryId())
                .unit(p.getUnit()).basePrice(p.getBasePrice()).vatRate(p.getVatRate())
                .description(p.getDescription()).build();
    }
}
