package vn.com.be_crm.application.product.query;

import vn.com.be_crm.application.product.dto.ProductResult;
import vn.com.be_crm.application.product.mapper.ProductCommandMapper;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.application.shared.dto.PageResult;
import vn.com.be_crm.application.shared.usecase.IUseCase;
import vn.com.be_crm.domain.product.repository.IProductRepository;

import java.util.stream.Collectors;

/** Use case lấy danh sách hàng hóa có phân trang. */
public class ListProductUseCase implements IUseCase<PageRequest, PageResult<ProductResult>> {
    private final IProductRepository repo;
    /** @param repo port lưu trữ */
    public ListProductUseCase(IProductRepository repo) { this.repo = repo; }
    /**
     * Lấy danh sách Product theo tham số phân trang.
     * @param r tham số phân trang @return PageResult
     */
    @Override
    public PageResult<ProductResult> execute(PageRequest r) {
        var p = repo.findAll(r);
        return PageResult.<ProductResult>builder()
                .items(p.getItems().stream().map(ProductCommandMapper::toResult).collect(Collectors.toList()))
                .total(p.getTotal()).page(p.getPage()).size(p.getSize()).build();
    }
}
