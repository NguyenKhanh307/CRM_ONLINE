package vn.com.be_crm.application.warehouse.dto;

/** Một dòng dữ liệu Warehouse từ file import. */
public record ImportWarehouseRowCommand(
        String name,
        String code,
        String address
) {}
