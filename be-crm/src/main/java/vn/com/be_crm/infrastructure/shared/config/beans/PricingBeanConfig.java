package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.query.*;
import vn.com.be_crm.application.shared.tx.ITransactionRunner;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductCategoryRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;
import vn.com.be_crm.domain.product.repository.IProductRepository;

/**
 * Wire các UseCase của module Pricing (policy + 4 bảng liên kết) qua @Bean.
 */
@Configuration
public class PricingBeanConfig {

    // ===== Price Policy =====

    /** @return CreatePricePolicyUseCase */
    @Bean public CreatePricePolicyUseCase createPricePolicyUseCase(IPricePolicyRepository r) { return new CreatePricePolicyUseCase(r); }
    /** @return UpdatePricePolicyUseCase */
    @Bean public UpdatePricePolicyUseCase updatePricePolicyUseCase(IPricePolicyRepository r) { return new UpdatePricePolicyUseCase(r); }
    /** @return DeletePricePolicyUseCase */
    @Bean public DeletePricePolicyUseCase deletePricePolicyUseCase(IPricePolicyRepository r) { return new DeletePricePolicyUseCase(r); }
    /** @return GetPricePolicyUseCase */
    @Bean public GetPricePolicyUseCase getPricePolicyUseCase(IPricePolicyRepository r) { return new GetPricePolicyUseCase(r); }
    /** @return ListPricePolicyUseCase */
    @Bean public ListPricePolicyUseCase listPricePolicyUseCase(IPricePolicyRepository r) { return new ListPricePolicyUseCase(r); }
    /** @return ImportBulkPricePolicyUseCase */
    @Bean public ImportBulkPricePolicyUseCase importBulkPricePolicyUseCase(IPricePolicyRepository r) { return new ImportBulkPricePolicyUseCase(r); }

    // ===== Price Policy Product =====

    /** @return CreatePricePolicyProductUseCase */
    @Bean public CreatePricePolicyProductUseCase createPricePolicyProductUseCase(IPricePolicyProductRepository r) { return new CreatePricePolicyProductUseCase(r); }
    /** @return UpdatePricePolicyProductUseCase */
    @Bean public UpdatePricePolicyProductUseCase updatePricePolicyProductUseCase(IPricePolicyProductRepository r) { return new UpdatePricePolicyProductUseCase(r); }
    /** @return DeletePricePolicyProductUseCase */
    @Bean public DeletePricePolicyProductUseCase deletePricePolicyProductUseCase(IPricePolicyProductRepository r) { return new DeletePricePolicyProductUseCase(r); }
    /** @return ListPricePolicyProductUseCase */
    @Bean public ListPricePolicyProductUseCase listPricePolicyProductUseCase(IPricePolicyProductRepository r) { return new ListPricePolicyProductUseCase(r); }
    /** @return ResolvePriceUseCase — tra cứu đơn giá theo chính sách giá */
    @Bean public vn.com.be_crm.application.pricing.query.ResolvePriceUseCase resolvePriceUseCase(IPricePolicyProductRepository r) {
        return new vn.com.be_crm.application.pricing.query.ResolvePriceUseCase(r);
    }

    // ===== Price Policy Customer =====

    /** @return CreatePricePolicyCustomerUseCase */
    @Bean public CreatePricePolicyCustomerUseCase createPricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new CreatePricePolicyCustomerUseCase(r); }
    /** @return DeletePricePolicyCustomerUseCase */
    @Bean public DeletePricePolicyCustomerUseCase deletePricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new DeletePricePolicyCustomerUseCase(r); }
    /** @return ListPricePolicyCustomerUseCase */
    @Bean public ListPricePolicyCustomerUseCase listPricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new ListPricePolicyCustomerUseCase(r); }

    // ===== Price Policy Product Category (doi ten tu Customer Category 2026-07-29) =====
    // Marker "chon nhanh": Create bulk-seed san pham thuoc danh muc vao price_policy_products.

    /** @return CreatePricePolicyProductCategoryUseCase */
    @Bean public CreatePricePolicyProductCategoryUseCase createPricePolicyProductCategoryUseCase(
            IPricePolicyProductCategoryRepository r, IProductRepository productRepo,
            IPricePolicyProductRepository policyProductRepo, ITransactionRunner tx) {
        return new CreatePricePolicyProductCategoryUseCase(r, productRepo, policyProductRepo, tx);
    }
    /** @return DeletePricePolicyProductCategoryUseCase */
    @Bean public DeletePricePolicyProductCategoryUseCase deletePricePolicyProductCategoryUseCase(IPricePolicyProductCategoryRepository r) { return new DeletePricePolicyProductCategoryUseCase(r); }
    /** @return ListPricePolicyProductCategoryUseCase */
    @Bean public ListPricePolicyProductCategoryUseCase listPricePolicyProductCategoryUseCase(IPricePolicyProductCategoryRepository r) { return new ListPricePolicyProductCategoryUseCase(r); }

    // ===== Price Policy Employee =====

    /** @return CreatePricePolicyEmployeeUseCase */
    @Bean public CreatePricePolicyEmployeeUseCase createPricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository r) { return new CreatePricePolicyEmployeeUseCase(r); }
    /** @return DeletePricePolicyEmployeeUseCase */
    @Bean public DeletePricePolicyEmployeeUseCase deletePricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository r) { return new DeletePricePolicyEmployeeUseCase(r); }
    /** @return ListPricePolicyEmployeeUseCase */
    @Bean public ListPricePolicyEmployeeUseCase listPricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository r) { return new ListPricePolicyEmployeeUseCase(r); }
}
