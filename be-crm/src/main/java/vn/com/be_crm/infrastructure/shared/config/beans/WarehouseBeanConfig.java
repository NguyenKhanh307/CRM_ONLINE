package vn.com.be_crm.infrastructure.shared.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.be_crm.application.warehouse.command.*;
import vn.com.be_crm.application.warehouse.query.*;
import vn.com.be_crm.domain.warehouse.repository.IInventoryStockRepository;
import vn.com.be_crm.domain.warehouse.repository.IWarehouseRepository;

/**
 * Wire các UseCase của module Warehouse (warehouse, inventory stock, import) qua @Bean.
 */
@Configuration
public class WarehouseBeanConfig {

    // ===== Warehouse =====

    /** @return CreateWarehouseUseCase */
    @Bean public CreateWarehouseUseCase createWarehouseUseCase(IWarehouseRepository r) { return new CreateWarehouseUseCase(r); }
    /** @return UpdateWarehouseUseCase */
    @Bean public UpdateWarehouseUseCase updateWarehouseUseCase(IWarehouseRepository r) { return new UpdateWarehouseUseCase(r); }
    /** @return DeleteWarehouseUseCase */
    @Bean public DeleteWarehouseUseCase deleteWarehouseUseCase(IWarehouseRepository r) { return new DeleteWarehouseUseCase(r); }
    /** @return GetWarehouseUseCase */
    @Bean public GetWarehouseUseCase getWarehouseUseCase(IWarehouseRepository r) { return new GetWarehouseUseCase(r); }
    /** @return ListWarehouseUseCase */
    @Bean public ListWarehouseUseCase listWarehouseUseCase(IWarehouseRepository r) { return new ListWarehouseUseCase(r); }

    // ===== Inventory Stock =====

    /** @return UpsertInventoryStockUseCase */
    @Bean public UpsertInventoryStockUseCase upsertInventoryStockUseCase(IInventoryStockRepository r) { return new UpsertInventoryStockUseCase(r); }
    /** @return GetInventoryStockUseCase */
    @Bean public GetInventoryStockUseCase getInventoryStockUseCase(IInventoryStockRepository r) { return new GetInventoryStockUseCase(r); }
    /** @return ListInventoryStockUseCase */
    @Bean public ListInventoryStockUseCase listInventoryStockUseCase(IInventoryStockRepository r) { return new ListInventoryStockUseCase(r); }

    // ===== Import =====

    /** @return ImportBulkWarehouseUseCase */
    @Bean public ImportBulkWarehouseUseCase importBulkWarehouseUseCase(IWarehouseRepository r) { return new ImportBulkWarehouseUseCase(r); }
}
