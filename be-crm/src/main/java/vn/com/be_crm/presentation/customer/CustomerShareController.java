package vn.com.be_crm.presentation.customer;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.be_crm.application.customer.command.AssignCustomerShareUseCase;
import vn.com.be_crm.application.customer.command.RevokeCustomerShareUseCase;
import vn.com.be_crm.application.customer.dto.AssignCustomerShareCommand;
import vn.com.be_crm.application.customer.dto.CustomerShareResult;
import vn.com.be_crm.application.customer.query.ListCustomerShareUseCase;
import vn.com.be_crm.presentation.shared.ApiResponse;

import java.util.List;

/**
 * REST controller cho nghiệp vụ chia sẻ khách hàng.
 */
@RestController
@RequestMapping("/api/customers/{customerId}/shares")
public class CustomerShareController {
    private final AssignCustomerShareUseCase assignUC;
    private final RevokeCustomerShareUseCase revokeUC;
    private final ListCustomerShareUseCase listUC;

    /**
     * @param assignUC use case gán chia sẻ @param revokeUC use case thu hồi @param listUC use case lấy danh sách
     */
    public CustomerShareController(AssignCustomerShareUseCase assignUC, RevokeCustomerShareUseCase revokeUC,
                                    ListCustomerShareUseCase listUC) {
        this.assignUC = assignUC; this.revokeUC = revokeUC; this.listUC = listUC;
    }

    /** Gán chia sẻ khách hàng. @param customerId ID khách hàng @param cmd JSON body @return 201 */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerShareResult>> assign(@PathVariable Long customerId,
                                                                    @Valid @RequestBody AssignCustomerShareCommand cmd) {
        return ResponseEntity.status(201).body(ApiResponse.created(assignUC.execute(
                AssignCustomerShareCommand.builder().customerId(customerId).userId(cmd.getUserId())
                        .permission(cmd.getPermission()).sharedBy(cmd.getSharedBy()).build())));
    }

    /** Lấy danh sách chia sẻ theo customerId. @param customerId ID khách hàng @return 200 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerShareResult>>> list(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.ok(listUC.execute(customerId)));
    }

    /** Thu hồi chia sẻ khách hàng. @param customerId ID khách hàng @param userId ID người dùng @return 204 */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> revoke(@PathVariable Long customerId, @PathVariable Long userId) {
        revokeUC.execute(new long[]{customerId, userId}); return ResponseEntity.noContent().build();
    }
}
