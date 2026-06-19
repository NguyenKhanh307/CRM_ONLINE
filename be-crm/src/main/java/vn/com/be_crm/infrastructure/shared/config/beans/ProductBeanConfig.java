package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.product.command.*;
import vn.com.be_crm.application.product.query.*;
import vn.com.be_crm.domain.product.repository.*;

/**
 * Wire các UseCase của module Product (category, product, trash, import) qua @Bean.
 */
@Configuration
public class ProductBeanConfig {

    // ===== Product Category =====

    /** @return CreateProductCategoryUseCase */
    @Bean public CreateProductCategoryUseCase createProductCategoryUseCase(IProductCategoryRepository r) { return new CreateProductCategoryUseCase(r); }
    /** @return UpdateProductCategoryUseCase */
    @Bean public UpdateProductCategoryUseCase updateProductCategoryUseCase(IProductCategoryRepository r) { return new UpdateProductCategoryUseCase(r); }
    /** @return DeleteProductCategoryUseCase */
    @Bean public DeleteProductCategoryUseCase deleteProductCategoryUseCase(IProductCategoryRepository r) { return new DeleteProductCategoryUseCase(r); }
    /** @return GetProductCategoryUseCase */
    @Bean public GetProductCategoryUseCase getProductCategoryUseCase(IProductCategoryRepository r) { return new GetProductCategoryUseCase(r); }
    /** @return ListProductCategoryUseCase */
    @Bean public ListProductCategoryUseCase listProductCategoryUseCase(IProductCategoryRepository r) { return new ListProductCategoryUseCase(r); }

    // ===== Product =====

    /** @return CreateProductUseCase */
    @Bean public CreateProductUseCase createProductUseCase(IProductRepository r) { return new CreateProductUseCase(r); }
    /** @return UpdateProductUseCase */
    @Bean public UpdateProductUseCase updateProductUseCase(IProductRepository r) { return new UpdateProductUseCase(r); }
    /** @return DeleteProductUseCase */
    @Bean public DeleteProductUseCase deleteProductUseCase(IProductRepository r) { return new DeleteProductUseCase(r); }
    /** @return GetProductUseCase */
    @Bean public GetProductUseCase getProductUseCase(IProductRepository r) { return new GetProductUseCase(r); }
    /** @return ListProductUseCase */
    @Bean public ListProductUseCase listProductUseCase(IProductRepository r) { return new ListProductUseCase(r); }

    // ===== Trash =====

    /** @return ListDeletedProductsUseCase */
    @Bean public ListDeletedProductsUseCase listDeletedProductsUseCase(IProductRepository r) { return new ListDeletedProductsUseCase(r); }
    /** @return RestoreProductUseCase */
    @Bean public RestoreProductUseCase restoreProductUseCase(IProductRepository r) { return new RestoreProductUseCase(r); }
    /** @return PurgeProductUseCase */
    @Bean public PurgeProductUseCase purgeProductUseCase(IProductRepository r) { return new PurgeProductUseCase(r); }

    // ===== Import =====

    /** @return ImportBulkProductUseCase */
    @Bean public ImportBulkProductUseCase importBulkProductUseCase(IProductRepository r) { return new ImportBulkProductUseCase(r); }
}
