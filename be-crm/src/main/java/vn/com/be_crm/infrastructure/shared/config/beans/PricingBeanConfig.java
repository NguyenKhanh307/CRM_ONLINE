package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.pricing.command.*;
import vn.com.be_crm.application.pricing.query.*;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerCategoryRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyCustomerRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyEmployeeRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyProductTypeRepository;
import vn.com.be_crm.domain.pricing.repository.IPricePolicyRepository;

/**
 * Wire các UseCase của module Pricing (policy + 5 bảng liên kết) qua @Bean.
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

    // ===== Price Policy Product =====

    /** @return CreatePricePolicyProductUseCase */
    @Bean public CreatePricePolicyProductUseCase createPricePolicyProductUseCase(IPricePolicyProductRepository r) { return new CreatePricePolicyProductUseCase(r); }
    /** @return UpdatePricePolicyProductUseCase */
    @Bean public UpdatePricePolicyProductUseCase updatePricePolicyProductUseCase(IPricePolicyProductRepository r) { return new UpdatePricePolicyProductUseCase(r); }
    /** @return DeletePricePolicyProductUseCase */
    @Bean public DeletePricePolicyProductUseCase deletePricePolicyProductUseCase(IPricePolicyProductRepository r) { return new DeletePricePolicyProductUseCase(r); }
    /** @return ListPricePolicyProductUseCase */
    @Bean public ListPricePolicyProductUseCase listPricePolicyProductUseCase(IPricePolicyProductRepository r) { return new ListPricePolicyProductUseCase(r); }

    // ===== Price Policy Customer =====

    /** @return CreatePricePolicyCustomerUseCase */
    @Bean public CreatePricePolicyCustomerUseCase createPricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new CreatePricePolicyCustomerUseCase(r); }
    /** @return DeletePricePolicyCustomerUseCase */
    @Bean public DeletePricePolicyCustomerUseCase deletePricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new DeletePricePolicyCustomerUseCase(r); }
    /** @return ListPricePolicyCustomerUseCase */
    @Bean public ListPricePolicyCustomerUseCase listPricePolicyCustomerUseCase(IPricePolicyCustomerRepository r) { return new ListPricePolicyCustomerUseCase(r); }

    // ===== Price Policy Customer Category =====

    /** @return CreatePricePolicyCustomerCategoryUseCase */
    @Bean public CreatePricePolicyCustomerCategoryUseCase createPricePolicyCustomerCategoryUseCase(IPricePolicyCustomerCategoryRepository r) { return new CreatePricePolicyCustomerCategoryUseCase(r); }
    /** @return DeletePricePolicyCustomerCategoryUseCase */
    @Bean public DeletePricePolicyCustomerCategoryUseCase deletePricePolicyCustomerCategoryUseCase(IPricePolicyCustomerCategoryRepository r) { return new DeletePricePolicyCustomerCategoryUseCase(r); }
    /** @return ListPricePolicyCustomerCategoryUseCase */
    @Bean public ListPricePolicyCustomerCategoryUseCase listPricePolicyCustomerCategoryUseCase(IPricePolicyCustomerCategoryRepository r) { return new ListPricePolicyCustomerCategoryUseCase(r); }

    // ===== Price Policy Product Type =====

    /** @return CreatePricePolicyProductTypeUseCase */
    @Bean public CreatePricePolicyProductTypeUseCase createPricePolicyProductTypeUseCase(IPricePolicyProductTypeRepository r) { return new CreatePricePolicyProductTypeUseCase(r); }
    /** @return DeletePricePolicyProductTypeUseCase */
    @Bean public DeletePricePolicyProductTypeUseCase deletePricePolicyProductTypeUseCase(IPricePolicyProductTypeRepository r) { return new DeletePricePolicyProductTypeUseCase(r); }
    /** @return ListPricePolicyProductTypeUseCase */
    @Bean public ListPricePolicyProductTypeUseCase listPricePolicyProductTypeUseCase(IPricePolicyProductTypeRepository r) { return new ListPricePolicyProductTypeUseCase(r); }

    // ===== Price Policy Employee =====

    /** @return CreatePricePolicyEmployeeUseCase */
    @Bean public CreatePricePolicyEmployeeUseCase createPricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository r) { return new CreatePricePolicyEmployeeUseCase(r); }
    /** @return DeletePricePolicyEmployeeUseCase */
    @Bean public DeletePricePolicyEmployeeUseCase deletePricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository r) { return new DeletePricePolicyEmployeeUseCase(r); }
    /** @return ListPricePolicyEmployeeUseCase */
    @Bean public ListPricePolicyEmployeeUseCase listPricePolicyEmployeeUseCase(IPricePolicyEmployeeRepository r) { return new ListPricePolicyEmployeeUseCase(r); }
}
