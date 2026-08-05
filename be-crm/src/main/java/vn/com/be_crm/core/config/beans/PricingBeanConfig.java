package vn.com.be_crm.core.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.query.*;
import vn.com.be_crm.core.tx.port.ITransactionRunner;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductCategoryRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;
import vn.com.be_crm.domain.product.repository.IProductRepository;

// wire các UseCase của module Pricing (policy + 3 bảng liên kết) qua @Bean
@Configuration
public class PricingBeanConfig {

    // ===== Price Policy =====

    @Bean public CreatePricePolicyUseCase createPricePolicyUseCase(IPricePolicyRepository r) { return new CreatePricePolicyUseCase(r); }
    @Bean public UpdatePricePolicyUseCase updatePricePolicyUseCase(IPricePolicyRepository r) { return new UpdatePricePolicyUseCase(r); }
    @Bean public DeletePricePolicyUseCase deletePricePolicyUseCase(IPricePolicyRepository r) { return new DeletePricePolicyUseCase(r); }
    @Bean public GetPricePolicyUseCase getPricePolicyUseCase(IPricePolicyRepository r) { return new GetPricePolicyUseCase(r); }
    @Bean public ListPricePolicyUseCase listPricePolicyUseCase(IPricePolicyRepository r) { return new ListPricePolicyUseCase(r); }
    @Bean public ImportBulkPricePolicyUseCase importBulkPricePolicyUseCase(IPricePolicyRepository r) { return new ImportBulkPricePolicyUseCase(r); }
    // danh sách chính sách hợp lệ cho form (lọc theo khách hàng nếu có)
    @Bean public ListEligiblePricePolicyUseCase listEligiblePricePolicyUseCase(
            IPricePolicyRepository r, IPricePolicyCustomerRepository customerRepo) {
        return new ListEligiblePricePolicyUseCase(r, customerRepo);
    }

    // ===== Price Policy Product =====

    @Bean public CreatePricePolicyProductUseCase createPricePolicyProductUseCase(IPricePolicyProductRepository r) { return new CreatePricePolicyProductUseCase(r); }
    @Bean public UpdatePricePolicyProductUseCase updatePricePolicyProductUseCase(IPricePolicyProductRepository r) { return new UpdatePricePolicyProductUseCase(r); }
    @Bean public DeletePricePolicyProductUseCase deletePricePolicyProductUseCase(IPricePolicyProductRepository r) { return new DeletePricePolicyProductUseCase(r); }
    @Bean public ListPricePolicyProductUseCase listPricePolicyProductUseCase(IPricePolicyProductRepository r) { return new ListPricePolicyProductUseCase(r); }
    // tra cứu đơn giá theo chính sách giá, có kiểm tra khách hàng áp dụng
    @Bean public vn.com.be_crm.application.pricing.query.ResolvePriceUseCase resolvePriceUseCase(
            IPricePolicyProductRepository r, IPricePolicyCustomerRepository customerRepo) {
        return new vn.com.be_crm.application.pricing.query.ResolvePriceUseCase(r, customerRepo);
    }

    // ===== Price Policy Customer =====

    @Bean public CreatePricePolicyCustomerUseCase createPricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new CreatePricePolicyCustomerUseCase(r); }
    @Bean public DeletePricePolicyCustomerUseCase deletePricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new DeletePricePolicyCustomerUseCase(r); }
    @Bean public ListPricePolicyCustomerUseCase listPricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new ListPricePolicyCustomerUseCase(r); }

    // ===== Price Policy Product Category (doi ten tu Customer Category 2026-07-29) =====
    // Marker "chon nhanh": Create bulk-seed san pham thuoc danh muc vao price_policy_products.

    @Bean public CreatePricePolicyProductCategoryUseCase createPricePolicyProductCategoryUseCase(
            IPricePolicyProductCategoryRepository r, IProductRepository productRepo,
            IPricePolicyProductRepository policyProductRepo, ITransactionRunner tx) {
        return new CreatePricePolicyProductCategoryUseCase(r, productRepo, policyProductRepo, tx);
    }
    @Bean public DeletePricePolicyProductCategoryUseCase deletePricePolicyProductCategoryUseCase(IPricePolicyProductCategoryRepository r) { return new DeletePricePolicyProductCategoryUseCase(r); }
    @Bean public ListPricePolicyProductCategoryUseCase listPricePolicyProductCategoryUseCase(IPricePolicyProductCategoryRepository r) { return new ListPricePolicyProductCategoryUseCase(r); }
}
