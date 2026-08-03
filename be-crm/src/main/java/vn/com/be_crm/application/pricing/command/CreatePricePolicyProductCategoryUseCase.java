package vn.com.be_crm.application.pricing.command;

import vn.com.be_crm.application.pricing.dto.CreatePricePolicyProductCategoryCommand;
import vn.com.be_crm.application.pricing.dto.PricePolicyProductCategoryResult;
import vn.com.be_crm.application.pricing.mapper.PricePolicySubEntityCommandMapper;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.core.usecase.IUseCase;
import vn.com.be_crm.domain.pricing.entity.PricePolicyProduct;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductCategoryRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.domain.product.entity.Product;
import vn.com.be_crm.domain.product.repository.IProductRepository;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case thêm danh mục sản phẩm vào chính sách giá — chọn 1 danh mục thì tự động bulk-seed
 * toàn bộ sản phẩm thuộc danh mục vào {@code price_policy_products} (giá để trống), người dùng
 * vào tab "Sản phẩm" sửa giá từng dòng như bình thường. Không lưu giá ở cấp danh mục vì các sản
 * phẩm cùng danh mục thường có giá gốc rất khác nhau — một giá cố định chung là sai bản chất.
 */
public class CreatePricePolicyProductCategoryUseCase implements IUseCase<CreatePricePolicyProductCategoryCommand, PricePolicyProductCategoryResult> {
    private final IPricePolicyProductCategoryRepository repo;
    private final IProductRepository productRepo;
    private final IPricePolicyProductRepository policyProductRepo;
    private final ITransactionRunner tx;

    /**
     * @param repo              port lưu trữ marker danh mục
     * @param productRepo       port tra sản phẩm theo danh mục
     * @param policyProductRepo port lưu trữ dòng giá theo sản phẩm — nơi bulk-seed ghi vào
     * @param tx                bộ chạy transaction (nhiều lệnh ghi phải cùng commit/rollback)
     */
    public CreatePricePolicyProductCategoryUseCase(IPricePolicyProductCategoryRepository repo, IProductRepository productRepo,
                                                     IPricePolicyProductRepository policyProductRepo, ITransactionRunner tx) {
        this.repo = repo; this.productRepo = productRepo; this.policyProductRepo = policyProductRepo; this.tx = tx;
    }

    /** Tạo mới PricePolicyProductCategory + bulk-seed sản phẩm chưa có giá riêng. @param cmd @return result */
    @Override public PricePolicyProductCategoryResult execute(CreatePricePolicyProductCategoryCommand cmd) {
        return tx.call(() -> {
            PricePolicyProductCategoryResult result = PricePolicySubEntityCommandMapper.toCategoryResult(
                    repo.save(PricePolicySubEntityCommandMapper.toCategoryEntity(cmd)));

            // Bỏ qua sản phẩm đã có dòng giá riêng trong chính sách này — tránh vi phạm
            // UNIQUE(policy,product) và tránh ghi đè giá đã chỉnh tay.
            Set<Long> existingProductIds = policyProductRepo.findAllByPricePolicyId(cmd.getPricePolicyId()).stream()
                    .map(PricePolicyProduct::getProductId).collect(Collectors.toSet());

            for (Product p : productRepo.findAllByCategoryId(cmd.getCategoryId())) {
                if (existingProductIds.contains(p.getId())) continue;
                policyProductRepo.save(PricePolicyProduct.builder()
                        .pricePolicyId(cmd.getPricePolicyId()).productId(p.getId()).build());
            }

            return result;
        });
    }
}
