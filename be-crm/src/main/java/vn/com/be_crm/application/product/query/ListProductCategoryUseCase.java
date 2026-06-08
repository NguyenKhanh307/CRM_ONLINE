package vn.com.be_crm.application.product.query;

import vn.com.be_crm.application.product.dto.ProductCategoryResult;
import vn.com.be_crm.application.product.mapper.ProductCategoryCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductCategoryRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách danh mục hàng hóa có phân trang. */
public class ListProductCategoryUseCase implements IUseCase<PageRequest, PageResult<ProductCategoryResult>> {
    private final IProductCategoryRepository repo;
    /** @param repo port lưu trữ */
    public ListProductCategoryUseCase(IProductCategoryRepository repo) { this.repo = repo; }
    /**
     * Lấy danh sách ProductCategory theo tham số phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<ProductCategoryResult> execute(PageRequest r) {
        var p = repo.findAll(r);
        return PageResult.<ProductCategoryResult>builder()
                .items(p.getItems().stream().map(ProductCategoryCommandMapper::toResult).collect(Collectors.toList()))
                .total(p.getTotal()).page(p.getPage()).size(p.getSize()).build();
    }
}
