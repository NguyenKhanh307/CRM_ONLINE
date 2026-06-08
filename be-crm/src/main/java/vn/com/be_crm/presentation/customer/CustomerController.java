package vn.com.be_crm.presentation.customer;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.customer.command.*;
import vn.com.be_crm.application.customer.dto.*;
import vn.com.be_crm.application.customer.query.*;
import vn.com.be_crm.application.shared.dto.PageRequest;
import vn.com.be_crm.presentation.shared.ApiResponse;
import vn.com.be_crm.presentation.shared.PageResponse;

/**
 * REST controller cho nghiệp vụ quản lý khách hàng.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CreateCustomerUseCase createUC;
    private final UpdateCustomerUseCase updateUC;
    private final DeleteCustomerUseCase deleteUC;
    private final GetCustomerUseCase getUC;
    private final ListCustomerUseCase listUC;

    /**
     * @param createUC use case tạo mới @param updateUC use case cập nhật @param deleteUC use case xóa mềm
     * @param getUC    use case lấy theo ID @param listUC use case lấy danh sách
     */
    public CustomerController(CreateCustomerUseCase createUC, UpdateCustomerUseCase updateUC,
                               DeleteCustomerUseCase deleteUC, GetCustomerUseCase getUC, ListCustomerUseCase listUC) {
        this.createUC = createUC; this.updateUC = updateUC; this.deleteUC = deleteUC;
        this.getUC = getUC; this.listUC = listUC;
    }

    /** Tạo mới khách hàng. @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResult>> create(@Valid @RequestBody CreateCustomerCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(createUC.execute(cmd)));
    }

    /** Lấy danh sách khách hàng có phân trang. @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CustomerResult>>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(listUC.execute(
                PageRequest.builder().page(page).size(size).sortBy(sortBy).sortDir(sortDir).build()))));
    }

    /** Lấy khách hàng theo ID. @param id ID @return 200 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResult>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(getUC.execute(id)));
    }

    /** Cập nhật khách hàng. @param id ID @param cmd JSON body @return 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResult>> update(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateCustomerCommand cmd) {
        return ResponseEntity.ok(ApiResponse.ok(updateUC.execute(
                UpdateCustomerCommand.builder().id(id).name(cmd.getName()).type(cmd.getType())
                        .taxCode(cmd.getTaxCode()).phone(cmd.getPhone()).email(cmd.getEmail())
                        .address(cmd.getAddress()).source(cmd.getSource()).status(cmd.getStatus())
                        .ownerId(cmd.getOwnerId()).unitId(cmd.getUnitId()).build())));
    }

    /** Xóa mềm khách hàng. @param id ID @return 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.execute(id); return ResponseEntity.noContent().build();
    }
}
